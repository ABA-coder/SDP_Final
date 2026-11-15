package Patterns.Facade;

import Patterns.FactoryMethod.Exercise;
import Patterns.FactoryMethod.ExerciseMode;
import Patterns.FactoryMethod.Word;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import Patterns.Observer.ProgressSnapshot;
import Patterns.Observer.WordProgress;
import Patterns.Strategy.SpacedRepetitionStrategy;
import Patterns.Strategy.WeakWordsStrategy;
import Patterns.Strategy.ReviewStrategy;

public final class ConsoleClient {
    private final Terminal terminal;
    private final PrintStream out;
    private final LearningApplication app;

    public ConsoleClient(LearningApplication app, Terminal terminal) {
        this.app = Objects.requireNonNull(app, "app");
        this.terminal = Objects.requireNonNull(terminal, "terminal");
        this.out = terminal.output();
    }

    public void run() throws IOException {
        out.println("\n============================================");
        out.println("  LINGUALAB | English -> Russian");
        out.println("  Learn words. Practice. Track your progress.");
        out.println("============================================");
        while (true) {
            ProgressSnapshot progress = app.progress();
            out.printf("%nXP: %d | Studied: %d/%d | Accuracy: %d%% | Streak: %d days%n",
                    progress.xp(), progress.studied(), app.vocabulary().size(), progress.accuracyPercent(), progress.dayStreak(app.today()));
            out.println("\n1. Start a lesson");
            out.println("2. Practice words");
            out.println("3. Show progress");
            out.println("4. Show vocabulary");
            out.println("0. Exit");
            int selection = terminal.number("Choose: ", 0, 4);
            if (selection == 0) {
                out.println("Progress saved. See you next time!");
                return;
            }
            switch (selection) {
                case 1 -> newLesson();
                case 2 -> practice();
                case 3 -> showProgress();
                case 4 -> showVocabulary();
                default -> throw new IllegalStateException("Unknown menu item.");
            }
        }
    }

    private void newLesson() throws IOException {
        out.println("\n--- LEARN NEW WORDS ---");
        List<String> topics = app.topics();
        for (int i = 0; i < topics.size(); i++) out.println((i + 1) + ". " + topics.get(i));
        out.println("0. Back");
        int topic = terminal.number("Topic: ", 0, topics.size());
        if (topic == 0) return;
        String selectedTopic = topics.get(topic - 1);
        int size = app.topicSize(selectedTopic);
        int count = terminal.number("Number of words (1-" + size + ", 0 to go back): ", 0, size);
        if (count == 0) return;
        app.startLesson(selectedTopic, count);
        learnWords();
    }

    private void learnWords() throws IOException {
        StudySession session = app.studySession();
        out.println("\n--- LEARNING: " + session.topic().toUpperCase(Locale.ROOT) + " ---");
        out.println("Read the word, translation and example. Press Enter when you have studied the card.");
        while (!session.finished()) {
            Word word = session.current();
            out.printf("%n[%d/%d] Word: %s%n", session.index() + 1, session.words().size(), word.english());
            out.println("Translation: " + word.russian());
            out.println("Example: " + word.example());
            if (!confirmStudyCard()) return;
        }
        out.println("\nLearning complete! " + session.words().size() + " words studied.");
        out.println("Choose Practice words to test yourself and earn XP.");
    }

    private boolean confirmStudyCard() throws IOException {
        while (true) {
            String command = terminal.line("Press Enter to mark studied and continue, or 0 to go back: ").strip();
            if (command.equals("0") || command.equalsIgnoreCase("q")) {
                out.println("Returned to menu. Confirmed learning cards are saved.");
                return false;
            }
            if (!command.isEmpty()) {
                out.println("Press Enter after reading the card, or enter 0 to go back.");
                continue;
            }
            try {
                app.learnCurrentWord();
                return true;
            } catch (IOException failure) {
                out.println("Could not save this card: " + failure.getMessage());
                out.println("Repair the data folder and retry, or enter 0 to go back.");
            }
        }
    }

    private void practice() throws IOException {
        ReviewStrategy spaced = new SpacedRepetitionStrategy();
        ReviewStrategy weak = new WeakWordsStrategy();
        int due = app.reviewCount(spaced);
        int studied = app.reviewCount(weak);
        out.println("\n--- PRACTICE ---");
        out.println("1. Spaced repetition (" + due + " words due first)");
        out.println("   Prioritize studied words whose next review date has arrived.");
        out.println("2. Weak words (" + studied + " studied words ranked first)");
        out.println("   Prioritize studied words with the lowest accuracy.");
        out.println("Both modes fill your chosen count with new or other words when needed.");
        out.println("0. Back");
        int choice = terminal.number("Review method: ", 0, 2);
        if (choice == 0) return;

        ReviewStrategy strategy = choice == 1 ? spaced : weak;
        int available = app.vocabulary().size();
        if (available == 0) {
            out.println("The vocabulary is empty. No practice words are available.");
            return;
        }
        out.println("Available for practice: " + available + " words.");
        int count = terminal.number("Number of words (1-" + available + ", 0 to go back): ", 0, available);
        if (count == 0) return;
        Optional<ExerciseMode> mode = chooseMode();
        if (mode.isEmpty()) return;
        app.startReview(strategy, count, mode.get());
        practiceExercises();
    }

    private Optional<ExerciseMode> chooseMode() throws IOException {
        out.println("\n1. Multiple choice");
        out.println("2. Type the Russian translation");
        out.println("0. Back");
        return switch (terminal.number("Exercise type: ", 0, 2)) {
            case 1 -> Optional.of(ExerciseMode.CHOICE);
            case 2 -> Optional.of(ExerciseMode.TYPING);
            default -> Optional.empty();
        };
    }

    private void practiceExercises() throws IOException {
        LessonSession session = app.session();
        showPracticeIntroduction(session);
        while (!session.finished()) {
            showExercise(session);
            if (!answerCurrentExercise(session) || !advanceExercise()) return;
            out.println();
        }
        out.printf("Practice complete! %d/%d correct. Earned: %d XP.%n",
                session.correct(), session.lesson().exercises().size(), session.xp());
    }

    private void showPracticeIntroduction(LessonSession session) {
        out.println("\n--- PRACTICE: " + session.lesson().title().toUpperCase(Locale.ROOT) + " ---");
        out.println("Correct answer: 10 XP. With a hint: 5 XP. Incorrect: 0 XP.");
        out.println("Commands: h = hint, q = return to menu.\n");
    }

    private void showExercise(LessonSession session) {
        Exercise exercise = session.current();
        out.printf("[%d/%d] %s%n", session.index() + 1, session.lesson().exercises().size(), exercise.instruction());
        out.println("Word: " + exercise.word().english());
        for (int i = 0; i < exercise.options().size(); i++) {
            out.println("  " + (i + 1) + ". " + exercise.options().get(i));
        }
    }

    private boolean answerCurrentExercise(LessonSession session) throws IOException {
        while (!session.answered()) {
            String answer = terminal.line("Your answer: ").strip();
            if (answer.equalsIgnoreCase("q")) {
                out.println("Returned to menu. Submitted answers are saved.");
                return false;
            }
            if (answer.equalsIgnoreCase("h")) {
                out.println("Hint: " + app.revealHint().hint());
                continue;
            }
            submitAnswer(session.current(), answer);
        }
        return true;
    }

    private void submitAnswer(Exercise exercise, String input) {
        Optional<String> answer = resolveAnswer(exercise, input);
        if (answer.isEmpty()) return;
        try {
            AnswerResult result = app.submitAnswer(answer.get());
            showAnswerResult(exercise, result);
        } catch (IOException failure) {
            out.println("Could not save this answer: " + failure.getMessage());
            out.println("Repair the data folder and retry your answer, or enter q.");
        }
    }

    private Optional<String> resolveAnswer(Exercise exercise, String input) {
        if (input.isBlank()) {
            out.println("Enter an answer, h or q.");
            return Optional.empty();
        }
        if (exercise.options().isEmpty()) return Optional.of(input);
        try {
            int option = Integer.parseInt(input);
            if (option >= 1 && option <= exercise.options().size()) {
                return Optional.of(exercise.options().get(option - 1));
            }
        } catch (NumberFormatException invalid) {

        }
        out.println("Enter a choice number from 1 to " + exercise.options().size() + ", h or q.");
        return Optional.empty();
    }

    private void showAnswerResult(Exercise exercise, AnswerResult result) {
        if (result.correct()) out.println("Correct! +" + result.xp() + " XP.");
        else out.println("Not quite. Correct translation: " + result.expected());
        out.println("Example: " + exercise.word().example());
        out.println("Next review: " + result.nextReview());
    }

    private boolean advanceExercise() throws IOException {
        while (true) {
            try {
                app.nextExercise();
                return true;
            } catch (IOException failure) {
                out.println("Answer saved, but session completion could not be saved: " + failure.getMessage());
                if (terminal.line("Press Enter to retry, or q for the menu: ").strip().equalsIgnoreCase("q")) return false;
            }
        }
    }

    private void showProgress() throws IOException {
        ProgressSnapshot progress = app.progress();
        out.println("\n--- YOUR PROGRESS ---");
        out.println("XP: " + progress.xp());
        out.println("Completed sessions: " + progress.completedLessons());
        out.println("Words studied: " + progress.studied() + "/" + app.vocabulary().size());
        out.println("Words mastered (3 correct in a row): " + progress.mastered());
        out.println("Accuracy: " + progress.accuracyPercent() + "%");
        out.println("Day streak: " + progress.dayStreak(app.today()));
        out.println("Words due: " + app.dueCount());
        out.printf("%n%-13s %-12s %8s %10s %14s%n", "English", "Russian", "Attempts", "Accuracy", "Next review");
        out.println("--------------------------------------------------------------");
        for (Word word : app.vocabulary()) {
            WordProgress p = progress.forWord(word.id(), app.today());
            if (!p.studied()) continue;
            String accuracy = p.attempts() == 0 ? "Not tested" : Math.round(p.accuracy() * 100) + "%";
            out.printf("%-13s %-12s %8d %10s %14s%n", word.english(), word.russian(), p.attempts(),
                    accuracy, p.due().isAfter(app.today()) ? p.due().toString() : "Due today");
        }
        if (progress.studied() == 0) out.println("No words studied yet. Start a lesson!");
        out.println("\nData file: " + app.dataFile());
        waitForBack();
    }

    private void showVocabulary() throws IOException {
        out.println("\n--- VOCABULARY ---");
        for (String topic : app.topics()) {
            out.println("\n" + topic + ":");
            for (Word word : app.vocabulary()) if (word.topic().equals(topic)) {
                out.printf("  %-13s -> %-12s | %s%n", word.english(), word.russian(), word.example());
            }
        }
        waitForBack();
    }

    private void waitForBack() throws IOException {
        out.println("\n0. Back");
        while (!terminal.line("Choose: ").strip().equals("0")) {
            out.println("Enter 0 to go back.");
        }
    }

}
