package Patterns.FactoryMethod;

import java.util.List;

public final class TypingExerciseFactory extends ExerciseFactory {
    @Override protected Exercise createExercise(Word word, List<Word> vocabulary) {
        return new TypingExercise(word);
    }
}
