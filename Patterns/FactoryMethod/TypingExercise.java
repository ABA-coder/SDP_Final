package Patterns.FactoryMethod;

import java.util.List;
import java.util.Objects;

public record TypingExercise(Word word) implements Exercise {
    public TypingExercise { Objects.requireNonNull(word, "word"); }
    @Override public String instruction() { return "Type the Russian translation"; }
    @Override public List<String> options() { return List.of(); }
}
