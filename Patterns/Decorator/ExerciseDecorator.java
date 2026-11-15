package Patterns.Decorator;

import Patterns.FactoryMethod.Exercise;
import Patterns.FactoryMethod.Word;
import java.util.List;
import java.util.Objects;


public abstract class ExerciseDecorator implements Exercise {
    protected final Exercise wrapped;
    protected ExerciseDecorator(Exercise wrapped) { this.wrapped = Objects.requireNonNull(wrapped); }
    @Override public Word word() { return wrapped.word(); }
    @Override public String instruction() { return wrapped.instruction(); }
    @Override public List<String> options() { return wrapped.options(); }
    @Override public boolean accepts(String answer) { return wrapped.accepts(answer); }
    @Override public String hint() { return wrapped.hint(); }
    @Override public int reward() { return wrapped.reward(); }
}
