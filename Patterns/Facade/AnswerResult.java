package Patterns.Facade;

import java.time.LocalDate;

public record AnswerResult(boolean correct, String expected, int xp, LocalDate nextReview) { }
