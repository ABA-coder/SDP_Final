package Patterns.Builder;

import Patterns.FactoryMethod.Exercise;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public final class VocabularyLessonBuilder implements LessonBuilder {
    private String title;
    private String topic;
    private final List<Exercise> exercises = new ArrayList<>();

    @Override public VocabularyLessonBuilder title(String title) {
        this.title = title;
        return this;
    }

    @Override public VocabularyLessonBuilder topic(String topic) {
        this.topic = topic;
        return this;
    }

    @Override public VocabularyLessonBuilder addExercise(Exercise exercise) {
        exercises.add(Objects.requireNonNull(exercise, "exercise"));
        return this;
    }

    @Override public Lesson build() {
        return new Lesson(title, topic, exercises);
    }
}
