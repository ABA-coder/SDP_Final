package Patterns.Strategy;

import Patterns.FactoryMethod.Word;
import java.time.LocalDate;
import java.util.List;
import Patterns.Observer.ProgressSnapshot;


public interface ReviewStrategy {
    String name();
    List<Word> select(List<Word> vocabulary, ProgressSnapshot progress, int limit, LocalDate today);
}
