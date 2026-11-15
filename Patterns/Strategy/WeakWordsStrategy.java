package Patterns.Strategy;

import Patterns.FactoryMethod.Word;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import Patterns.Observer.ProgressSnapshot;

public final class WeakWordsStrategy implements ReviewStrategy {
    @Override public String name() { return "Weak words"; }
    @Override public List<Word> select(List<Word> vocabulary, ProgressSnapshot progress, int limit, LocalDate today) {
        return vocabulary.stream().filter(w -> progress.forWord(w.id(), today).studied())
                .sorted(Comparator.comparingDouble((Word w) -> progress.forWord(w.id(), today).accuracy())
                        .thenComparingInt(w -> progress.forWord(w.id(), today).streak())
                        .thenComparing(Word::english))
                .limit(Math.max(0, limit)).toList();
    }
}
