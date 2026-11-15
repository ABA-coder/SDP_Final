package Patterns.Observer;

import java.io.PrintStream;

public final class ObserverClient {
    public void attachConsoleObservers(ProgressSubject subject, ProgressSnapshot initial, PrintStream output) {
        subject.subscribe(new ConsoleProgressObserver(output));
        subject.subscribe(new AchievementObserver(initial, output));
    }
}
