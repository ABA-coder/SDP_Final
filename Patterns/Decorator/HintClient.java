package Patterns.Decorator;

import Patterns.FactoryMethod.Exercise;


public final class HintClient {
    public Exercise addHint(Exercise exercise) {
        return new HintDecorator(exercise);
    }
}
