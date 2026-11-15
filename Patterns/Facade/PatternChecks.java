package Patterns.Facade;

import Patterns.Builder.LessonBuilderClient;
import Patterns.Builder.VocabularyLessonBuilder;
import Patterns.Builder.LessonDirector;
import Patterns.Builder.Lesson;
import Patterns.Decorator.HintClient;
import Patterns.Decorator.HintDecorator;
import Patterns.FactoryMethod.ExerciseFactoryClient;
import Patterns.FactoryMethod.ChoiceExerciseFactory;
import Patterns.FactoryMethod.TypingExerciseFactory;
import Patterns.FactoryMethod.ChoiceExercise;
import Patterns.FactoryMethod.TypingExercise;
import Patterns.FactoryMethod.Exercise;
import Patterns.FactoryMethod.ExerciseMode;
import Patterns.Observer.ProgressPublisher;
import Patterns.Observer.ProgressSnapshot;
import Patterns.Observer.WordProgress;
import Patterns.Strategy.ReviewClient;
import Patterns.Strategy.SpacedRepetitionStrategy;
import Patterns.Strategy.WeakWordsStrategy;
import Patterns.Strategy.ReviewContext;
import Patterns.Strategy.ReviewStrategy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public final class PatternChecks {
    private static final LocalDate TODAY = LocalDate.of(2026, 11, 14);
    private static final Clock CLOCK = Clock.fixed(TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);
    private static int checks;

    private PatternChecks() { }

    public static void run() throws IOException {
        checks = 0;
        Path temporary = Files.createTempDirectory("lingualab-check-").toAbsolutePath().normalize();
        try {
            constructionAndDecoration();
            strategiesAndDates();
            practiceAndPersistence(temporary);
            learningCardsAndPersistence(temporary);
            learningPreservesPractice(temporary);
            shortLessonsReachNewWords(temporary);
            storageFailures(temporary);
            consoleNavigation(temporary);
            reviewSizeNavigation(temporary);
            learningNavigation(temporary);
            System.out.println("PASS: " + checks + " checks; all six patterns, persistence and console navigation.");
        } finally {
            Path temporaryRoot = Path.of(System.getProperty("java.io.tmpdir")).toAbsolutePath().normalize();
            if (!temporary.getParent().equals(temporaryRoot)
                    || !temporary.getFileName().toString().startsWith("lingualab-check-")) {
                throw new IOException("Unexpected test cleanup location: " + temporary);
            }
            try (var paths = Files.walk(temporary)) {
                for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) Files.deleteIfExists(path);
            }
        }
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    private static void rejects(Runnable operation, String message) {
        boolean rejected = false;
        try { operation.run(); }
        catch (IllegalArgumentException | IllegalStateException | UnsupportedOperationException expected) {
            rejected = true;
        }
        check(rejected, message);
    }

    private static ExerciseFactoryClient factories() {
        return new ExerciseFactoryClient(Map.of(
                ExerciseMode.CHOICE, new ChoiceExerciseFactory(),
                ExerciseMode.TYPING, new TypingExerciseFactory()));
    }

    private static LanguageLearningFacade application(Path file, ProgressPublisher publisher) throws IOException {
        var progress = new ProgressFacade(new FileProgressRepository(file), publisher, CLOCK);
        var lessons = new LessonBuilderClient(new LessonDirector(factories()));
        return new LanguageLearningFacade(new InMemoryVocabularyRepository(), progress, lessons,
                new ReviewClient(), new HintClient(), new SpacedRepetitionStrategy());
    }

    private static void constructionAndDecoration() {
        var words = new InMemoryVocabularyRepository().load();
        Exercise choice = factories().create(ExerciseMode.CHOICE, words.get(0), words);
        Exercise typing = factories().create(ExerciseMode.TYPING, words.get(0), words);
        check(choice instanceof ChoiceExercise && typing instanceof TypingExercise, "Factory Method products");
        check(choice.options().size() == 4 && choice.options().contains("привет"), "Choice has four valid options");
        rejects(() -> new ChoiceExercise(words.get(0), List.of("wrong", "also wrong")), "Choices require a correct answer");
        rejects(() -> new ChoiceExercise(words.get(0), List.of("привет", "привет")), "Duplicate choices rejected");
        check(typing.accepts("  ПРИВЕТ  ") && !typing.accepts("hello"), "Typing normalization and grading");
        Exercise hinted = new HintClient().addHint(typing);
        check(hinted instanceof HintDecorator && hinted.reward() == 5 && typing.reward() == 10,
                "Decorator changes the reward without changing its component");
        check(hinted.accepts("привет") && hinted.hint().contains("Starts with"), "Decorator keeps grading and adds a hint");
        var builder = new VocabularyLessonBuilder().title("Test").topic("Everyday").addExercise(choice);
        Lesson original = builder.build();
        builder.addExercise(typing);
        check(original.exercises().size() == 1 && builder.build().exercises().size() == 2, "Builder creates independent products");
        rejects(() -> original.exercises().clear(), "Lesson exercises are immutable");
        rejects(() -> new VocabularyLessonBuilder().title("Empty").topic("Everyday").build(), "Empty lesson rejected");
        Lesson directed = new LessonBuilderClient(new LessonDirector(factories()))
                .create("Directed", "Everyday", words.subList(0, 2), words, ExerciseMode.TYPING);
        check(directed.exercises().size() == 2 && directed.exercises().get(1) instanceof TypingExercise,
                "Director uses the chosen builder and factory");
    }

    private static void strategiesAndDates() {
        var words = new InMemoryVocabularyRepository().load().subList(0, 3);
        var progress = new ProgressSnapshot(Map.of(
                words.get(0).id(), new WordProgress(2, 2, 2, 2, TODAY.plusDays(2), TODAY),
                words.get(1).id(), new WordProgress(3, 1, 0, 0, TODAY, TODAY)), 20, 1,
                Set.of(TODAY, TODAY.minusDays(1)));
        var due = new ReviewClient().select(new SpacedRepetitionStrategy(), words, progress, 10, TODAY);
        check(due.size() == 1 && due.get(0).equals(words.get(1)), "Due strategy excludes new and future words");
        var weak = new ReviewClient().select(new WeakWordsStrategy(), words, progress, 10, TODAY);
        check(weak.size() == 2 && weak.get(0).equals(words.get(1)), "Weak strategy chooses lowest accuracy first");
        var practice = new ReviewClient();
        check(practice.selectForPractice(new SpacedRepetitionStrategy(), words, progress, 3, TODAY)
                        .equals(List.of(words.get(1), words.get(2), words.get(0))),
                "Spaced practice fills the requested count with due, new, then future words");
        check(practice.selectForPractice(new WeakWordsStrategy(), words, progress, 3, TODAY)
                        .equals(List.of(words.get(1), words.get(0), words.get(2))),
                "Weak practice preserves accuracy priority and fills the count with new words");
        var context = new ReviewContext(new SpacedRepetitionStrategy());
        check(context.select(words, progress, 10, TODAY).size() == 1, "Context starts with due strategy");
        context.setStrategy(new WeakWordsStrategy());
        check(context.select(words, progress, 10, TODAY).size() == 2, "Same context switches strategy at runtime");
        rejects(() -> new ReviewContext(new WeakWordsStrategy()).select(words, progress, 0, TODAY),
                "Review context rejects an invalid count");
        check(progress.dayStreak(TODAY) == 2 && progress.dayStreak(TODAY.plusDays(1)) == 2
                && progress.dayStreak(TODAY.plusDays(2)) == 0, "Day streak boundaries");
        WordProgress p = WordProgress.unseen(TODAY).reviewed(true, TODAY);
        check(p.intervalDays() == 1 && p.due().equals(TODAY.plusDays(1)), "First correct answer schedules tomorrow");
        p = p.reviewed(true, TODAY.plusDays(1));
        check(p.intervalDays() == 2 && p.due().equals(TODAY.plusDays(3)), "Second correct answer doubles interval");
        p = p.reviewed(true, TODAY.plusDays(3));
        check(p.mastered() && p.intervalDays() == 4, "Three correct answers give mastery");
        p = p.reviewed(false, TODAY.plusDays(7));
        check(!p.mastered() && p.intervalDays() == 0 && p.due().equals(TODAY.plusDays(7)), "Mistake resets mastery and interval");
        for (int i = 0; i < 12; i++) p = p.reviewed(true, TODAY);
        check(p.intervalDays() == 60, "Review interval has a 60-day cap");
    }

    private static void practiceAndPersistence(Path temporary) throws IOException {
        Path file = temporary.resolve("learning/progress.properties");
        var publisher = new ProgressPublisher();
        var eventsA = new AtomicInteger();
        var eventsB = new AtomicInteger();
        publisher.subscribe(p -> eventsA.incrementAndGet());
        publisher.subscribe(p -> eventsB.incrementAndGet());
        var app = application(file, publisher);
        var session = app.startReview(new WeakWordsStrategy(), 2, ExerciseMode.TYPING);
        app.revealHint();
        app.revealHint();
        var first = app.submitAnswer("привет");
        check(first.correct() && first.xp() == 5 && app.progress().xp() == 5, "Facade applies one hint and rewards 5 XP");
        check(eventsA.get() == 1 && eventsB.get() == 1, "Two observers receive saved progress");
        boolean rejected = false;
        try { app.submitAnswer("привет"); } catch (IllegalStateException expected) { rejected = true; }
        check(rejected && app.progress().xp() == 5, "Duplicate submission cannot add points");
        app.nextExercise();
        var wrong = app.submitAnswer("wrong");
        check(!wrong.correct() && wrong.xp() == 0 && app.dueCount() == 1, "Mistake is due immediately");
        app.nextExercise();
        check(session.finished() && session.correct() == 1 && session.xp() == 5, "Session records its result");
        check(app.progress().completedLessons() == 1 && eventsA.get() == 3 && eventsB.get() == 3,
                "Completion is saved and published");
        var restarted = application(file, new ProgressPublisher());
        check(restarted.progress().equals(app.progress()), "Restart restores the full snapshot");
        restarted.startReview(new SpacedRepetitionStrategy(), 1, ExerciseMode.CHOICE);
        check(restarted.session().lesson().exercises().size() == 1
                && restarted.session().current().word().id().equals("thanks"), "Due review uses saved history");
        restarted.submitAnswer("спасибо");
        restarted.nextExercise();
        check(restarted.dueCount() == 0 && restarted.progress().xp() == 15, "Correct review clears due word and rewards XP");
        restarted.startReview(new SpacedRepetitionStrategy(), 8, ExerciseMode.CHOICE);
        check(restarted.session().lesson().exercises().size() == 8
                        && restarted.progress().forWord(restarted.session().current().word().id(), TODAY).attempts() == 0
                        && restarted.dueCount() == 0,
                "Practice fills an empty due queue with new words without changing the due count");
        restarted.startReview(new WeakWordsStrategy(), 8, ExerciseMode.TYPING);
        check(restarted.session().current().word().id().equals("thanks"), "Changing strategy changes word ordering");
        rejects(() -> restarted.startLesson("Missing", 1), "Unknown topic rejected");
    }

    private static void learningCardsAndPersistence(Path temporary) throws IOException {
        Path file = temporary.resolve("cards/progress.properties");
        var publisher = new ProgressPublisher();
        var events = new AtomicInteger();
        publisher.subscribe(p -> events.incrementAndGet());
        var app = application(file, publisher);
        var cards = app.startLesson("Everyday", 2);
        check(cards.words().size() == 2 && cards.current().id().equals("hello")
                        && app.progress().studied() == 0, "Opening a learning card does not record progress before confirmation");
        rejects(() -> cards.words().clear(), "Learning cards are immutable");
        rejects(() -> app.session(), "Learning does not expose an active quiz session");

        app.learnCurrentWord();
        var hello = app.progress().forWord("hello", TODAY);
        check(hello.studied() && hello.attempts() == 0 && hello.correct() == 0 && !hello.mastered()
                        && app.progress().xp() == 0 && cards.index() == 1,
                "Confirming a card records study without a fake answer, reward or mastery");
        check(app.dueCount() == 1 && app.reviewCount(new WeakWordsStrategy()) == 1 && events.get() == 1,
                "Confirmed cards are eligible for both review priorities and notify observers");
        var restarted = application(file, new ProgressPublisher());
        check(restarted.progress().equals(app.progress()) && restarted.progress().completedLessons() == 0,
                "A confirmed card survives restart without completing an unfinished lesson");

        app.learnCurrentWord();
        check(cards.finished() && app.progress().studied() == 2 && app.progress().completedLessons() == 1
                        && app.progress().xp() == 0 && app.progress().accuracyPercent() == 0 && events.get() == 2,
                "Learning completion is saved once without grading the cards");
        boolean rejected = false;
        try { app.learnCurrentWord(); } catch (IllegalStateException expected) { rejected = true; }
        check(rejected && app.progress().completedLessons() == 1 && events.get() == 2,
                "Confirming an already completed lesson cannot duplicate completion");
        check(application(file, new ProgressPublisher()).progress().equals(app.progress()),
                "Completed learning progress restores from the existing file format");
    }

    private static void learningPreservesPractice(Path temporary) throws IOException {
        var app = application(temporary.resolve("learn-known/progress.properties"), new ProgressPublisher());
        for (int attempt = 0; attempt < 3; attempt++) {
            app.startReview(new WeakWordsStrategy(), 1, ExerciseMode.TYPING);
            app.submitAnswer("привет");
            app.nextExercise();
        }
        WordProgress practiced = app.progress().forWord("hello", TODAY);
        check(practiced.mastered() && app.progress().xp() == 30, "Practice creates the mastered-word baseline");
        app.startLesson("Everyday", 8);
        while (!app.studySession().finished()) app.learnCurrentWord();
        check(app.progress().forWord("hello", TODAY).equals(practiced)
                        && app.progress().xp() == 30 && app.progress().accuracyPercent() == 100
                        && app.progress().mastered() == 1 && app.progress().completedLessons() == 4,
                "Reading learned cards preserves practice attempts, accuracy, schedule, mastery and XP");
    }

    private static void storageFailures(Path temporary) throws IOException {
        Path blocked = temporary.resolve("blocked");
        var publisher = new ProgressPublisher();
        var events = new AtomicInteger();
        publisher.subscribe(p -> events.incrementAndGet());
        var app = application(blocked.resolve("progress.properties"), publisher);
        app.startReview(new WeakWordsStrategy(), 1, ExerciseMode.TYPING);
        Files.writeString(blocked, "Cannot become a directory.");
        boolean failed = false;
        try { app.submitAnswer("привет"); } catch (IOException expected) { failed = true; }
        check(failed && app.progress().xp() == 0 && !app.session().answered() && events.get() == 0,
                "Failed save leaves the answer retryable and sends no event");
        Files.delete(blocked);
        app.submitAnswer("привет");
        check(app.progress().xp() == 10 && app.session().answered(), "Answer can be retried after storage repair");
        Path corrupt = temporary.resolve("corrupt.properties");
        String invalid = "version=1\nxp=broken\n";
        Files.writeString(corrupt, invalid);
        failed = false;
        try { new FileProgressRepository(corrupt).load(); } catch (IOException expected) { failed = true; }
        check(failed && Files.readString(corrupt).equals(invalid), "Corrupt data is reported and preserved");

        Path cardBlocked = temporary.resolve("card-blocked");
        var cardPublisher = new ProgressPublisher();
        var cardEvents = new AtomicInteger();
        cardPublisher.subscribe(p -> cardEvents.incrementAndGet());
        var cards = application(cardBlocked.resolve("progress.properties"), cardPublisher);
        cards.startLesson("Everyday", 1);
        Files.writeString(cardBlocked, "Cannot become a directory.");
        failed = false;
        try { cards.learnCurrentWord(); } catch (IOException expected) { failed = true; }
        check(failed && cards.studySession().index() == 0 && cards.progress().studied() == 0
                        && cards.progress().completedLessons() == 0 && cardEvents.get() == 0,
                "Failed card save preserves the card, progress and completion and sends no event");
        Files.delete(cardBlocked);
        cards.learnCurrentWord();
        check(cards.studySession().finished() && cards.progress().studied() == 1
                        && cards.progress().completedLessons() == 1 && cards.progress().xp() == 0 && cardEvents.get() == 1,
                "Retrying a card after storage repair saves study and completion exactly once");
    }

    private static void shortLessonsReachNewWords(Path temporary) throws IOException {
        var app = application(temporary.resolve("short-lessons/progress.properties"), new ProgressPublisher());
        Set<String> visited = new java.util.HashSet<>();
        for (int lesson = 0; lesson < 4; lesson++) {
            app.startLesson("Everyday", 2);
            while (!app.studySession().finished()) {
                visited.add(app.studySession().current().id());
                app.learnCurrentWord();
            }
        }
        check(visited.size() == 8 && app.progress().studied() == 8,
                "Four short lessons reach every word in the topic");
    }

    private static void consoleNavigation(Path temporary) throws IOException {
        var app = application(temporary.resolve("menu/progress.properties"), new ProgressPublisher());
        var bytes = new ByteArrayOutputStream();
        try (var output = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            new ConsoleClient(app, new Terminal(new StringReader("5\n3\nx\n0\n4\n2\n0\n0\n"), output)).run();
        }
        String text = bytes.toString(StandardCharsets.UTF_8);
        check(!text.contains("Show design patterns"), "Menu has no pattern option");
        check(text.contains("Enter a number from 0 to 4."), "Option 5 is rejected");
        check(text.contains("--- YOUR PROGRESS ---") && text.contains("--- VOCABULARY ---"), "Both information screens show content");
        check(text.split("1\\. Start a lesson", -1).length - 1 == 3, "Main menu returns only after Back");
        check(text.split("Enter 0 to go back\\.", -1).length - 1 == 2, "Other inputs keep information screens open");
    }

    private static void reviewSizeNavigation(Path temporary) throws IOException {
        for (int method = 1; method <= 2; method++) {
            var app = application(temporary.resolve("review-size-" + method + "/progress.properties"),
                    new ProgressPublisher());
            ReviewStrategy strategy = method == 1 ? new SpacedRepetitionStrategy() : new WeakWordsStrategy();
            check(app.reviewCount(strategy) == 0, "New profile has no priority review words");

            for (int count : List.of(5, 11, 3)) {
                String text = consoleOutput(app, "2\n" + method + "\n" + count + "\n2\n"
                        + "wrong\n".repeat(count) + "0\n");
                var exercises = app.session().lesson().exercises();
                check(exercises.size() == count
                                && exercises.stream().map(e -> e.word().id()).distinct().count() == count,
                        "Practice method " + method + " creates exactly " + count + " distinct words");
                check(text.contains("[" + count + "/" + count + "]")
                                && text.contains("Practice complete! 0/" + count + " correct."),
                        "Practice method " + method + " completes the newly requested size " + count);
            }
            check(app.progress().studied() == 11 && app.progress().completedLessons() == 3,
                    "Larger practice adds new words and all three sessions are saved");
            int maximum = app.vocabulary().size();
            app.startReview(strategy, maximum, ExerciseMode.TYPING);
            var full = app.session().lesson().exercises();
            check(full.size() == maximum
                            && full.stream().map(e -> e.word().id()).distinct().count() == maximum,
                    "Practice method " + method + " supports the entire vocabulary without duplicates");
            rejects(() -> app.startReview(strategy, maximum + 1, ExerciseMode.TYPING),
                    "Practice method " + method + " rejects more words than the vocabulary contains");
            rejects(() -> app.startReview(strategy, 0, ExerciseMode.TYPING),
                    "Practice method " + method + " rejects zero words");
        }
    }

    private static void learningNavigation(Path temporary) throws IOException {
        var app = application(temporary.resolve("learning-ui/progress.properties"), new ProgressPublisher());
        String text = consoleOutput(app, "1\n1\n2\n\n\n0\n");
        check(text.contains("hello") && text.contains("привет") && text.contains("thank you")
                        && text.contains("спасибо") && text.contains("Hello! It is nice to meet you."),
                "Learning cards show the English word, Russian translation and usage example");
        check(!text.contains("Your answer:") && !text.contains("Multiple choice")
                        && !text.contains("Hint:") && !text.contains("Correct answer: 10 XP"),
                "Learning has no quiz, exercise-mode, hint or grading prompts");
        check(app.progress().studied() == 2 && app.progress().completedLessons() == 1 && app.progress().xp() == 0,
                "Enter confirms each learning card and finishes the lesson without XP");
        for (String back : List.of("0", "q")) {
            var cancelled = application(temporary.resolve("learning-cancel-" + back + "/progress.properties"),
                    new ProgressPublisher());
            consoleOutput(cancelled, "1\n1\n2\n" + back + "\n0\n");
            check(cancelled.progress().studied() == 0 && cancelled.progress().completedLessons() == 0,
                    "Learning Back command " + back + " does not confirm the displayed card");
        }
        Path partialFile = temporary.resolve("learning-partial/progress.properties");
        var partial = application(partialFile, new ProgressPublisher());
        consoleOutput(partial, "1\n1\n2\n\n0\n0\n");
        var restored = application(partialFile, new ProgressPublisher());
        check(restored.progress().studied() == 1 && restored.progress().forWord("hello", TODAY).studied()
                        && !restored.progress().forWord("thanks", TODAY).studied()
                        && restored.progress().completedLessons() == 0,
                "Leaving a partial lesson keeps only confirmed cards across restart");
    }

    private static String consoleOutput(LearningApplication app, String input) throws IOException {
        var bytes = new ByteArrayOutputStream();
        try (var output = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            new ConsoleClient(app, new Terminal(new StringReader(input), output)).run();
        }
        return bytes.toString(StandardCharsets.UTF_8);
    }
}
