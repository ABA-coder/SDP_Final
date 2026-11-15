package Patterns.Strategy;

import Patterns.FactoryMethod.Word;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import Patterns.Observer.ProgressSnapshot;

public final class SpacedRepetitionStrategy implements ReviewStrategy {
    @Override public String name() { return "Spaced repetition"; }
    @Override public List<Word> select(List<Word> vocabulary, ProgressSnapshot progress, int limit, LocalDate today) {

        return vocabulary.stream()
                .filter(w -> progress.forWord(w.id(), today).studied())
                .filter(w -> !progress.forWord(w.id(), today).due().isAfter(today))
                .sorted(Comparator.comparing((Word w) -> progress.forWord(w.id(), today).due())
                        .thenComparing(Word::english))
                .limit(Math.max(0, limit)).toList();
    }
}
