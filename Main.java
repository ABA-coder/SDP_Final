import Patterns.Builder.LessonBuilderClient;
import Patterns.Builder.LessonDirector;
import Patterns.Decorator.HintClient;
import Patterns.Facade.ProgressFacade;
import Patterns.Facade.ConsoleClient;
import Patterns.Facade.EndOfInputException;
import Patterns.Facade.PatternChecks;
import Patterns.Facade.Terminal;
import Patterns.Facade.InMemoryVocabularyRepository;
import Patterns.Facade.FileProgressRepository;
import Patterns.Facade.LanguageLearningFacade;
import Patterns.Facade.LearningApplication;
import Patterns.FactoryMethod.ExerciseFactoryClient;
import Patterns.FactoryMethod.ChoiceExerciseFactory;
import Patterns.FactoryMethod.TypingExerciseFactory;
import Patterns.FactoryMethod.ExerciseMode;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Map;
import Patterns.Observer.ObserverClient;
import Patterns.Observer.ProgressPublisher;
import Patterns.Strategy.ReviewClient;
import Patterns.Strategy.SpacedRepetitionStrategy;


public final class Main {
    private Main() { }

    public static void main(String[] args) {
        try {
            if (args.length == 1 && args[0].equals("--test")) {
                PatternChecks.run();
                return;
            }
            if (args.length != 0) {
                System.err.println("Usage: java -cp out Main [--test]");
                System.exit(1);
            }
            Console console = System.console();
            Reader reader = console == null
                    ? new InputStreamReader(System.in, StandardCharsets.UTF_8) : console.reader();
            Path data = Path.of(System.getProperty("lingualab.data", "data"), "progress.properties");

            var publisher = new ProgressPublisher();
            var progress = new ProgressFacade(new FileProgressRepository(data), publisher, Clock.systemDefaultZone());
            var factories = new ExerciseFactoryClient(Map.of(
                    ExerciseMode.CHOICE, new ChoiceExerciseFactory(),
                    ExerciseMode.TYPING, new TypingExerciseFactory()));
            var lessons = new LessonBuilderClient(new LessonDirector(factories));
            LearningApplication application = new LanguageLearningFacade(new InMemoryVocabularyRepository(),
                    progress, lessons, new ReviewClient(), new HintClient(), new SpacedRepetitionStrategy());
            new ObserverClient().attachConsoleObservers(publisher, application.progress(), System.out);
            new ConsoleClient(application, new Terminal(reader, System.out)).run();
        } catch (EndOfInputException end) {
            System.out.println("\nInput closed. Your saved learning and practice progress is kept. Goodbye!");
        } catch (IOException failure) {
            System.err.println("Cannot open or read application data: " + failure.getMessage());
            System.exit(1);
        } catch (AssertionError failure) {
            System.err.println("FAIL: " + failure.getMessage());
            System.exit(1);
        }
    }
}
