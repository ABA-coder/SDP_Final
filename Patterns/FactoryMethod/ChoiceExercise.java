package Patterns.FactoryMethod;

import java.util.List;
import java.util.Objects;
import java.util.HashSet;

public record ChoiceExercise(Word word, List<String> options) implements Exercise {
    public ChoiceExercise {
        Objects.requireNonNull(word, "word");
        options = List.copyOf(options);
        if (options.size() < 2 || !options.contains(word.russian())
                || new HashSet<>(options).size() != options.size()) {
            throw new IllegalArgumentException("Choices must be unique and contain the correct translation.");
        }
    }
    @Override public String instruction() { return "Choose the Russian translation"; }
}
