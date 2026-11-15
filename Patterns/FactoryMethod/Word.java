package Patterns.FactoryMethod;



public record Word(String id, String english, String russian, String topic, String example) {
    public Word {
        if (id == null || id.isBlank() || english == null || english.isBlank()
                || russian == null || russian.isBlank() || topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("A word needs an ID, translations and a topic.");
        }
    }
}
