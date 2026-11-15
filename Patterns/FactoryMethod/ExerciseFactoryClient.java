package Patterns.FactoryMethod;

import java.util.List;
import java.util.Map;
import java.util.Objects;


public final class ExerciseFactoryClient {
    private final Map<ExerciseMode, ExerciseFactory> factories;

    public ExerciseFactoryClient(Map<ExerciseMode, ExerciseFactory> factories) {
        this.factories = Map.copyOf(factories);
    }

    public Exercise create(ExerciseMode mode, Word word, List<Word> vocabulary) {
        ExerciseFactory factory = factories.get(Objects.requireNonNull(mode, "mode"));
        if (factory == null) throw new IllegalArgumentException("Unsupported exercise mode: " + mode);
        return factory.create(word, vocabulary);
    }
}
