package Patterns.Facade;

import Patterns.Builder.Lesson;
import Patterns.FactoryMethod.Exercise;

public final class LessonSession {
    private final Lesson lesson;
    private int index;
    private int correct;
    private int xp;
    private boolean answered;
    private boolean hintUsed;
    private Exercise exercise;

    public LessonSession(Lesson lesson) {
        this.lesson = lesson;
        exercise = lesson.exercises().get(0);
    }
    public Lesson lesson() { return lesson; }
    public int index() { return index; }
    public int correct() { return correct; }
    public int xp() { return xp; }
    public boolean answered() { return answered; }
    public boolean finished() { return index == lesson.exercises().size(); }
    public boolean last() { return index == lesson.exercises().size() - 1; }
    public Exercise current() {
        if (finished()) throw new IllegalStateException("This lesson is finished.");
        return exercise;
    }
    public boolean hintUsed() { return hintUsed; }
    void applyHint(Exercise decorated) {
        if (answered || finished()) throw new IllegalStateException("This question is already answered.");
        if (!current().word().equals(decorated.word())) throw new IllegalArgumentException("Hint for another word.");
        if (!hintUsed) { exercise = decorated; hintUsed = true; }
    }
    void record(boolean accepted, int reward) {
        if (answered || finished()) throw new IllegalStateException("Answer already submitted.");
        answered = true;
        if (accepted) correct++;
        xp += reward;
    }
    void advance() {
        if (!answered || finished()) throw new IllegalStateException("Submit an answer first.");
        index++;
        answered = false;
        hintUsed = false;
        if (!finished()) exercise = lesson.exercises().get(index);
    }
}
