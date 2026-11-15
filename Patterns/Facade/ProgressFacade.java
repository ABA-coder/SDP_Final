package Patterns.Facade;

import Patterns.FactoryMethod.Exercise;
import Patterns.FactoryMethod.Word;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import Patterns.Observer.ProgressSnapshot;
import Patterns.Observer.WordProgress;
import Patterns.Observer.ProgressSubject;


public final class ProgressFacade {
    private final ProgressRepository repository;
    private final ProgressSubject publisher;
    private final Clock clock;
    private ProgressSnapshot progress;

    public ProgressFacade(ProgressRepository repository, ProgressSubject publisher, Clock clock) throws IOException {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.publisher = Objects.requireNonNull(publisher, "publisher");
        this.clock = Objects.requireNonNull(clock, "clock");
        progress = repository.load();
    }

    public ProgressSnapshot snapshot() { return progress; }
    public LocalDate today() { return LocalDate.now(clock); }
    public Path dataFile() { return repository.file(); }

    public AnswerResult recordAnswer(Exercise exercise, String answer) throws IOException {
        if (answer == null || answer.isBlank()) throw new IllegalArgumentException("Enter an answer first.");
        boolean correct = exercise.accepts(answer);
        int reward = correct ? exercise.reward() : 0;
        WordProgress updated = progress.forWord(exercise.word().id(), today()).reviewed(correct, today());
        Map<String, WordProgress> words = new HashMap<>(progress.words());
        words.put(exercise.word().id(), updated);
        Set<LocalDate> days = new HashSet<>(progress.studyDays());
        days.add(today());
        save(new ProgressSnapshot(words, progress.xp() + reward, progress.completedLessons(), days));
        return new AnswerResult(correct, exercise.word().russian(), reward, updated.due());
    }

    public void completeSession() throws IOException {
        save(new ProgressSnapshot(progress.words(), progress.xp(), progress.completedLessons() + 1,
                progress.studyDays()));
    }

    public void recordStudy(Word word, boolean completedSession) throws IOException {
        Map<String, WordProgress> words = new HashMap<>(progress.words());
        words.put(word.id(), progress.forWord(word.id(), today()).studiedOn(today()));
        Set<LocalDate> days = new HashSet<>(progress.studyDays());
        days.add(today());
        save(new ProgressSnapshot(words, progress.xp(),
                progress.completedLessons() + (completedSession ? 1 : 0), days));
    }

    public void notifyObservers() { publisher.publish(progress); }

    private void save(ProgressSnapshot next) throws IOException {
        repository.save(next);
        progress = next;
    }
}
