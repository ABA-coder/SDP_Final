package Patterns.Facade;

import Patterns.FactoryMethod.Word;
import java.util.List;


public final class StudySession {
    private final String topic;
    private final List<Word> words;
    private int index;

    public StudySession(String topic, List<Word> words) {
        if (topic == null || topic.isBlank() || words == null || words.isEmpty()) {
            throw new IllegalArgumentException("A study session needs a topic and words.");
        }
        this.topic = topic;
        this.words = List.copyOf(words);
    }

    public String topic() { return topic; }
    public List<Word> words() { return words; }
    public int index() { return index; }
    public boolean finished() { return index == words.size(); }
    public boolean last() { return index == words.size() - 1; }
    public Word current() {
        if (finished()) throw new IllegalStateException("This learning session is finished.");
        return words.get(index);
    }

    void advance() {
        if (finished()) throw new IllegalStateException("This learning session is finished.");
        index++;
    }
}
