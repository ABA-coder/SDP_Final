package Patterns.Observer;



@FunctionalInterface
public interface ProgressObserver {
    void onProgressChanged(ProgressSnapshot progress);
}
