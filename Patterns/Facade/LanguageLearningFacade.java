package Patterns.Facade;

import Patterns.Builder.LessonBuilderClient;
import Patterns.Decorator.HintClient;
import Patterns.FactoryMethod.Exercise;
import Patterns.FactoryMethod.ExerciseMode;
import Patterns.FactoryMethod.Word;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Comparator;
import java.util.Objects;
import Patterns.Observer.ProgressSnapshot;
import Patterns.Strategy.ReviewClient;
import Patterns.Strategy.ReviewStrategy;


public final class LanguageLearningFacade implements LearningApplication {
    private final List<Word> vocabulary;
    private final ProgressFacade progress;
    private final LessonBuilderClient lessons;
    private final ReviewClient reviews;
    private final HintClient hints;
    private final ReviewStrategy dueStrategy;
    private LessonSession session;
    private StudySession studySession;

    public LanguageLearningFacade(VocabularyRepository vocabulary, ProgressFacade progress,
                                  LessonBuilderClient lessons, ReviewClient reviews,
                                  HintClient hints, ReviewStrategy dueStrategy) {
        this.vocabulary = List.copyOf(vocabulary.load());
        this.progress = Objects.requireNonNull(progress, "progress");
        this.lessons = Objects.requireNonNull(lessons, "lessons");
        this.reviews = Objects.requireNonNull(reviews, "reviews");
        this.hints = Objects.requireNonNull(hints, "hints");
        this.dueStrategy = Objects.requireNonNull(dueStrategy, "dueStrategy");
    }

    @Override public LocalDate today() { return progress.today(); }
    @Override public List<Word> vocabulary() { return vocabulary; }
    @Override public List<String> topics() { return vocabulary.stream().map(Word::topic).distinct().toList(); }
    @Override public int topicSize(String topic) {
        return (int) vocabulary.stream().filter(word -> word.topic().equals(topic)).count();
    }
    @Override public ProgressSnapshot progress() { return progress.snapshot(); }
    @Override public Path dataFile() { return progress.dataFile(); }
    @Override public int dueCount() { return reviewCount(dueStrategy); }
    @Override public int reviewCount(ReviewStrategy strategy) {
        if (vocabulary.isEmpty()) return 0;
        return reviews.select(strategy, vocabulary, progress(), vocabulary.size(), today()).size();
    }
    @Override public LessonSession session() {
        if (session == null) throw new IllegalStateException("Start a practice session first.");
        return session;
    }

    @Override public StudySession studySession() {
        if (studySession == null) throw new IllegalStateException("Start a learning session first.");
        return studySession;
    }

    @Override public StudySession startLesson(String topic, int count) {
        if (count < 1 || count > topicSize(topic)) throw new IllegalArgumentException("Invalid lesson size.");

        List<Word> words = vocabulary.stream()
                .filter(word -> word.topic().equals(topic))
                .sorted(Comparator.comparing((Word word) -> progress().forWord(word.id(), today()).studied())
                        .thenComparing(word -> progress().forWord(word.id(), today()).lastReviewed(),
                                Comparator.nullsFirst(Comparator.naturalOrder())))
                .limit(count)
                .toList();
        studySession = new StudySession(topic, words);
        session = null;
        return studySession;
    }

    @Override public void learnCurrentWord() throws IOException {
        StudySession active = studySession();
        progress.recordStudy(active.current(), active.last());
        active.advance();
        progress.notifyObservers();
    }

    @Override public LessonSession startReview(ReviewStrategy strategy, int count, ExerciseMode mode) {
        List<Word> words = reviews.selectForPractice(strategy, vocabulary, progress(), count, today());
        return start(strategy.name(), "Review", words, mode);
    }

    private LessonSession start(String title, String topic, List<Word> words, ExerciseMode mode) {
        session = new LessonSession(lessons.create(title, topic, words, vocabulary, mode));
        studySession = null;
        return session;
    }

    @Override public Exercise revealHint() {
        LessonSession active = session();
        if (active.answered()) throw new IllegalStateException("This question is already answered.");
        if (!active.hintUsed()) active.applyHint(hints.addHint(active.current()));
        return active.current();
    }

    @Override public AnswerResult submitAnswer(String answer) throws IOException {
        LessonSession active = session();
        if (active.answered()) throw new IllegalStateException("Answer already submitted.");
        AnswerResult result = progress.recordAnswer(active.current(), answer);
        active.record(result.correct(), result.xp());
        progress.notifyObservers();
        return result;
    }

    @Override public void nextExercise() throws IOException {
        LessonSession active = session();
        if (active.finished() || !active.answered()) throw new IllegalStateException("Submit an answer first.");
        boolean completed = active.last();
        if (completed) progress.completeSession();
        active.advance();
        if (completed) progress.notifyObservers();
    }
}
