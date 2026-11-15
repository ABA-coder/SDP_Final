package Patterns.Facade;

import Patterns.FactoryMethod.Word;
import java.util.List;

public final class InMemoryVocabularyRepository implements VocabularyRepository {

    @Override public List<Word> load() {
        return List.of(
            new Word("hello", "hello", "привет", "Everyday", "Hello! It is nice to meet you."),
            new Word("thanks", "thank you", "спасибо", "Everyday", "Thank you for your help."),
            new Word("friend", "friend", "друг", "Everyday", "My friend lives nearby."),
            new Word("family", "family", "семья", "Everyday", "My family is at home."),
            new Word("morning", "morning", "утро", "Everyday", "I read in the morning."),
            new Word("evening", "evening", "вечер", "Everyday", "We walk in the evening."),
            new Word("book", "book", "книга", "Everyday", "This book is interesting."),
            new Word("house", "house", "дом", "Everyday", "Our house has a small garden."),
            new Word("water", "water", "вода", "Food & drink", "May I have some water?"),
            new Word("bread", "bread", "хлеб", "Food & drink", "We buy fresh bread."),
            new Word("apple", "apple", "яблоко", "Food & drink", "I eat an apple every day."),
            new Word("milk", "milk", "молоко", "Food & drink", "There is milk in the fridge."),
            new Word("cheese", "cheese", "сыр", "Food & drink", "Would you like some cheese?"),
            new Word("coffee", "coffee", "кофе", "Food & drink", "She drinks coffee in the morning."),
            new Word("tea", "tea", "чай", "Food & drink", "A cup of tea, please."),
            new Word("dinner", "dinner", "ужин", "Food & drink", "Dinner is ready."),
            new Word("ticket", "ticket", "билет", "Travel", "I need a train ticket."),
            new Word("airport", "airport", "аэропорт", "Travel", "The airport is outside the city."),
            new Word("train", "train", "поезд", "Travel", "The train leaves at nine."),
            new Word("hotel", "hotel", "отель", "Travel", "Our hotel is near the beach."),
            new Word("street", "street", "улица", "Travel", "This street is very quiet."),
            new Word("city", "city", "город", "Travel", "I love this city."),
            new Word("map", "map", "карта", "Travel", "Can you show me on the map?"),
            new Word("luggage", "luggage", "багаж", "Travel", "My luggage is heavy.")
        );
    }
}
