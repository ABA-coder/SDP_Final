package Patterns.Strategy;

import Patterns.FactoryMethod.Word;
import java.time.LocalDate;
import java.util.List;
import java.util.Comparator;
import java.util.LinkedHashSet;
import Patterns.Observer.ProgressSnapshot;

public final class ReviewClient {
    private final ReviewContext context = new ReviewContext(new SpacedRepetitionStrategy());

    public List<Word> select(ReviewStrategy strategy, List<Word> vocabulary,
                             ProgressSnapshot progress, int count, LocalDate today) {
        context.setStrategy(strategy);
        return context.select(vocabulary, progress, count, today);
    }


    public List<Word> selectForPractice(ReviewStrategy strategy, List<Word> vocabulary,
                                        ProgressSnapshot progress, int count, LocalDate today) {
        if (count < 1 || count > vocabulary.size()) {
            throw new IllegalArgumentException("Practice size must be between 1 and " + vocabulary.size() + ".");
        }
        var selected = new LinkedHashSet<>(select(strategy, vocabulary, progress, count, today));
        vocabulary.stream()
                .filter(word -> !selected.contains(word))
                .sorted(Comparator.comparing(word -> progress.forWord(word.id(), today).studied()))
                .limit(count - selected.size())
                .forEach(selected::add);
        return List.copyOf(selected);
    }
}
