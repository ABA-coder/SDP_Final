package Patterns.Decorator;

import Patterns.FactoryMethod.Exercise;

public final class HintDecorator extends ExerciseDecorator {
    public HintDecorator(Exercise wrapped) { super(wrapped); }
    @Override public String hint() {
        String answer = word().russian();
        return "Starts with \u201c" + answer.substring(0, 1) + "\u201d, "
                + answer.codePointCount(0, answer.length()) + " letters. Example: " + word().example();
    }
    @Override public int reward() { return Math.min(5, wrapped.reward()); }
}
