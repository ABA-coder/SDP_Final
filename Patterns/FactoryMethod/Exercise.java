package Patterns.FactoryMethod;

import java.util.List;
import java.util.Locale;

public interface Exercise {
    Word word();
    String instruction();
    List<String> options();
    default String hint() { return ""; }
    default int reward() { return 10; }
    default boolean accepts(String answer) {
        return answer != null && normalize(answer).equals(normalize(word().russian()));
    }
    private static String normalize(String value) {
        return value.strip().toLowerCase(Locale.ROOT).replace('ё', 'е').replaceAll("\\s+", " ");
    }
}
