package Patterns.Builder;

import Patterns.FactoryMethod.Exercise;

public interface LessonBuilder {
    LessonBuilder title(String title);
    LessonBuilder topic(String topic);
    LessonBuilder addExercise(Exercise exercise);
    Lesson build();
}
