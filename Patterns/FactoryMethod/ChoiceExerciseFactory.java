package Patterns.FactoryMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ChoiceExerciseFactory extends ExerciseFactory {
    @Override protected Exercise createExercise(Word word, List<Word> vocabulary) {
        var alternatives = new ArrayList<>(vocabulary.stream()
                .map(Word::russian).filter(s -> !s.equals(word.russian())).distinct().toList());
        Collections.shuffle(alternatives);
        var choices = new ArrayList<>(alternatives.subList(0, Math.min(3, alternatives.size())));
        choices.add(word.russian());
        Collections.shuffle(choices);
        return new ChoiceExercise(word, choices);
    }
}
