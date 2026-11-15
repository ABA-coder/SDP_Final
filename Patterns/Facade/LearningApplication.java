package Patterns.Facade;

import Patterns.FactoryMethod.Exercise;
import Patterns.FactoryMethod.ExerciseMode;
import Patterns.FactoryMethod.Word;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import Patterns.Observer.ProgressSnapshot;
import Patterns.Strategy.ReviewStrategy;


public interface LearningApplication {
    LocalDate today();
    List<Word> vocabulary();
    List<String> topics();
    int topicSize(String topic);
    ProgressSnapshot progress();
    Path dataFile();
    int dueCount();
    int reviewCount(ReviewStrategy strategy);
    LessonSession session();
    StudySession studySession();
    StudySession startLesson(String topic, int count);
    void learnCurrentWord() throws IOException;
    LessonSession startReview(ReviewStrategy strategy, int count, ExerciseMode mode);
    Exercise revealHint();
    AnswerResult submitAnswer(String answer) throws IOException;
    void nextExercise() throws IOException;
}
