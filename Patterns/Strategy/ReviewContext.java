package Patterns.Strategy;

import Patterns.FactoryMethod.Word;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import Patterns.Observer.ProgressSnapshot;

public final class ReviewContext {
    private ReviewStrategy strategy;

    public ReviewContext(ReviewStrategy strategy) {
        setStrategy(strategy);
    }

    public void setStrategy(ReviewStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy");
    }

    public List<Word> select(List<Word> vocabulary, ProgressSnapshot progress, int count, LocalDate today) {
        if (count < 1) throw new IllegalArgumentException("Review size must be positive.");
        return List.copyOf(strategy.select(vocabulary, progress, count, today));
    }
}
