package Patterns.Observer;

import java.time.LocalDate;

public record WordProgress(int attempts, int correct, int streak, int intervalDays,
                           LocalDate due, LocalDate lastReviewed) {
    public WordProgress {
        if (attempts < 0 || correct < 0 || correct > attempts || streak < 0
                || streak > correct || intervalDays < 0 || intervalDays > 60 || due == null) {
            throw new IllegalArgumentException("Invalid word progress.");
        }
    }
    public static WordProgress unseen(LocalDate today) {
        return new WordProgress(0, 0, 0, 0, today, null);
    }
    public boolean studied() { return attempts > 0 || lastReviewed != null; }

    public WordProgress studiedOn(LocalDate today) {
        return studied() ? this : new WordProgress(0, 0, 0, 0, today, today);
    }
    public WordProgress reviewed(boolean accepted, LocalDate today) {
        int interval = accepted ? (intervalDays == 0 ? 1 : Math.min(60, intervalDays * 2)) : 0;
        return new WordProgress(attempts + 1, correct + (accepted ? 1 : 0),
                accepted ? streak + 1 : 0, interval, today.plusDays(interval), today);
    }
    public double accuracy() { return attempts == 0 ? 0 : (double) correct / attempts; }
    public boolean mastered() { return streak >= 3; }
}
