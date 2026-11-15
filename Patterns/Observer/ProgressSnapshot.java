package Patterns.Observer;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

public record ProgressSnapshot(Map<String, WordProgress> words, int xp, int completedLessons,
                               Set<LocalDate> studyDays) {
    public ProgressSnapshot {
        if (xp < 0 || completedLessons < 0) throw new IllegalArgumentException("Negative progress totals.");
        words = Map.copyOf(words);
        studyDays = Set.copyOf(studyDays);
    }
    public static ProgressSnapshot empty() { return new ProgressSnapshot(Map.of(), 0, 0, Set.of()); }
    public WordProgress forWord(String id, LocalDate today) {
        return words.getOrDefault(id, WordProgress.unseen(today));
    }
    public int studied() { return (int) words.values().stream().filter(WordProgress::studied).count(); }
    public int mastered() { return (int) words.values().stream().filter(WordProgress::mastered).count(); }
    public int accuracyPercent() {
        int attempts = words.values().stream().mapToInt(WordProgress::attempts).sum();
        int correct = words.values().stream().mapToInt(WordProgress::correct).sum();
        return attempts == 0 ? 0 : (int) Math.round(100.0 * correct / attempts);
    }
    public int dayStreak(LocalDate today) {
        LocalDate day = studyDays.contains(today) ? today : today.minusDays(1);
        int count = 0;
        while (studyDays.contains(day)) { count++; day = day.minusDays(1); }
        return count;
    }
}
