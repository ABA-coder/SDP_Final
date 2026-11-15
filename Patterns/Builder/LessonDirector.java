package Patterns.Builder;

import Patterns.FactoryMethod.ExerciseFactoryClient;
import Patterns.FactoryMethod.ExerciseMode;
import Patterns.FactoryMethod.Word;
import java.util.List;
import java.util.Objects;


public final class LessonDirector {
    private final ExerciseFactoryClient exercises;

    public LessonDirector(ExerciseFactoryClient exercises) {
        this.exercises = Objects.requireNonNull(exercises, "exercises");
    }

    public Lesson construct(LessonBuilder builder, String title, String topic,
                            List<Word> words, List<Word> vocabulary, ExerciseMode mode) {
        builder.title(title).topic(topic);
        for (Word word : words) builder.addExercise(exercises.create(mode, word, vocabulary));
        return builder.build();
    }
}
