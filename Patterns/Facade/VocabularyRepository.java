package Patterns.Facade;

import Patterns.FactoryMethod.Word;
import java.util.List;

public interface VocabularyRepository {
    List<Word> load();
}
