package Patterns.FactoryMethod;

import java.util.List;
import java.util.Objects;


public abstract class ExerciseFactory {
    public final Exercise create(Word word, List<Word> vocabulary) {
        Objects.requireNonNull(word, "word");
        Objects.requireNonNull(vocabulary, "vocabulary");
        return createExercise(word, vocabulary);
    }
    protected abstract Exercise createExercise(Word word, List<Word> vocabulary);
}
