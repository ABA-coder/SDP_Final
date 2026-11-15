package Patterns.Observer;

import java.io.PrintStream;
import java.util.Objects;

public final class ConsoleProgressObserver implements ProgressObserver {
    private final PrintStream output;

    public ConsoleProgressObserver(PrintStream output) {
        this.output = Objects.requireNonNull(output, "output");
    }

    @Override public void onProgressChanged(ProgressSnapshot progress) {
        output.printf("[Saved] Total XP: %d | Words studied: %d | Accuracy: %d%%%n",
                progress.xp(), progress.studied(), progress.accuracyPercent());
    }
}
