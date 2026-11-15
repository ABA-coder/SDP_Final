package Patterns.Observer;


public interface ProgressSubject {
    void subscribe(ProgressObserver observer);
    void unsubscribe(ProgressObserver observer);
    void publish(ProgressSnapshot progress);
}
