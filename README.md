# LinguaLab

LinguaLab is our Java project for learning English vocabulary with Russian translations. We separated learning and practice so that a person can first read and understand a word, then test their knowledge. The application has 24 words divided into three topics: Everyday, Food & drink, and Travel.

## Team contributions

We divided the six design patterns between three team members. Each member contributed their pattern code and UML diagrams from their own GitHub account.

| Team member | Patterns | Contribution |
| --- | --- | --- |
| Abylay | Builder and Decorator | Building practice lessons and adding hints to exercises. |
| Allazhar | Facade and Factory Method | Coordinating the application and creating multiple-choice and typing exercises. |
| Dias | Observer and Strategy | Sending progress notifications and selecting words for review. |

The Git history contains each member's contributions. The overall project UML and the shared project files are included in the same repository.

## Main features

### Start a lesson

This mode teaches words through cards. Each card shows an English word, its Russian translation, and an example sentence. Words that have not been studied appear first.

Pressing Enter marks the current card as studied and moves to the next one. Entering `0` or `q` returns to the main menu. Confirmed cards stay saved even if the lesson is not finished.

Reading a card does not give XP or count as a correct answer. A newly studied word becomes due for review that day. Reading an existing card again keeps its previous results and review date.

### Practice words

Practice tests vocabulary with multiple-choice questions or typed Russian translations. A session can contain any number from 1 to 24 distinct words. Choosing 5 words in one session does not limit the next session: choosing 11 gives 11 words.

There are two review methods:

- **Spaced repetition** gives priority to studied words whose review date has arrived.
- **Weak words** gives priority to studied words with the lowest answer accuracy.

If the priority list is too short, the application adds new words and then other remaining words until it reaches the selected count.

During practice, `h` shows a hint and `q` returns to the menu. A correct answer gives 10 XP, a correct answer with a hint gives 5 XP, and an incorrect answer gives 0 XP.

### Progress and vocabulary

The progress screen shows XP, completed sessions, studied and mastered words, accuracy, the daily streak, and review dates. A word becomes mastered after three correct answers in a row. The vocabulary screen shows all words with their translations and examples. Both screens stay open until `0` is entered.

Progress is saved locally in `data/progress.properties` after confirmed cards and submitted answers. It is restored when the application is opened again. An unfinished session does not resume at the same position, but its saved progress is kept.

## Design patterns

We used six design patterns to separate the main responsibilities of the application.

| Pattern | Type | Use in the project |
| --- | --- | --- |
| Builder | Creational | `LessonDirector` uses `LessonBuilder` to assemble an immutable set of practice exercises. |
| Factory Method | Creational | Concrete factories create `ChoiceExercise` or `TypingExercise` through the same creation workflow. |
| Decorator | Structural | `HintDecorator` wraps an exercise, adds a hint, and changes its reward. |
| Facade | Structural | `LanguageLearningFacade` gives the console one application API. `ProgressFacade` handles saving progress and sending notifications. |
| Strategy | Behavioral | `ReviewContext` switches between the spaced-repetition and weak-word selection algorithms. |
| Observer | Behavioral | `ProgressPublisher` notifies separate observers about saved progress and new mastery achievements. |

The patterns work together during practice. Strategy selects the words, Builder and Factory Method create the exercises, and Decorator adds hints when needed. Facade coordinates these operations, while Observer reports saved progress.

## Project structure

`Main.java` creates the dependencies and starts the application. All other Java types are inside the six pattern packages under `Patterns`. Each type has its own file.

We kept `StudySession` separate from `LessonSession`: the first handles learning cards, and the second handles scored practice. Both use the same progress storage. Interfaces and constructor injection separate storage, word selection, and exercise creation from the code that uses them.

## UML

The `UML` folder contains the project architecture and separate diagrams for each pattern. Each pattern has a class diagram showing its roles and relationships, and a sequence diagram showing how it is used. The diagrams are available as PNG, SVG, and editable PlantUML files. More details are in [UML/README.md](UML/README.md).
