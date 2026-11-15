package Patterns.Builder;

import Patterns.FactoryMethod.Exercise;
import java.util.List;


public record Lesson(String title, String topic, List<Exercise> exercises) {
    public Lesson {
        if (title == null || title.isBlank() || topic == null || topic.isBlank()
                || exercises == null || exercises.isEmpty()) {
            throw new IllegalArgumentException("A lesson needs a title, topic and exercises.");
        }
        exercises = List.copyOf(exercises);
    }
}
