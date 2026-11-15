package Patterns.Observer;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;


public final class ProgressPublisher implements ProgressSubject {
    private final CopyOnWriteArrayList<ProgressObserver> observers = new CopyOnWriteArrayList<>();

    @Override public void subscribe(ProgressObserver observer) {
        observers.addIfAbsent(Objects.requireNonNull(observer, "observer"));
    }

    @Override public void unsubscribe(ProgressObserver observer) {
        observers.remove(observer);
    }

    @Override public void publish(ProgressSnapshot progress) {
        for (ProgressObserver observer : observers) {
            try { observer.onProgressChanged(progress); }
            catch (RuntimeException failure) {
                System.err.println("A progress observer failed: " + failure.getMessage());
            }
        }
    }
}
