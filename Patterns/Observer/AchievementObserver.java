package Patterns.Observer;

import Patterns.FactoryMethod.Word;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class AchievementObserver implements ProgressObserver {
    private final PrintStream output;
    private Set<String> mastered;

    public AchievementObserver(ProgressSnapshot progress, PrintStream output) {
        this.output = Objects.requireNonNull(output, "output");
        mastered = masteredIds(progress);
    }

    @Override public void onProgressChanged(ProgressSnapshot progress) {
        Set<String> updated = masteredIds(progress);
        for (String id : updated) {
            if (!mastered.contains(id)) output.println("[Achievement] Word mastered: " + id
                    + " - 3 correct answers in a row!");
        }
        mastered = updated;
    }

    private static Set<String> masteredIds(ProgressSnapshot progress) {
        Set<String> result = new HashSet<>();
        progress.words().forEach((id, value) -> { if (value.mastered()) result.add(id); });
        return result;
    }
}
