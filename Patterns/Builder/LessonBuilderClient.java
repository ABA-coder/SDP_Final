package Patterns.Builder;

import Patterns.FactoryMethod.ExerciseMode;
import Patterns.FactoryMethod.Word;
import java.util.List;
import java.util.Objects;


public final class LessonBuilderClient {
    private final LessonDirector director;

    public LessonBuilderClient(LessonDirector director) {
        this.director = Objects.requireNonNull(director, "director");
    }

    public Lesson create(String title, String topic, List<Word> words,
                         List<Word> vocabulary, ExerciseMode mode) {
        return director.construct(new VocabularyLessonBuilder(), title, topic, words, vocabulary, mode);
    }
}
