# UML diagrams

We included an overall architecture diagram and two diagrams for each of the six design patterns used in LinguaLab.

The overall diagram is in `Project/architecture.png`. It shows `Main`, the six pattern packages, and the main relationships between them.

Each pattern folder contains:

- **classes.png** — the classes, their pattern roles, and their relationships.
- **usage.png** — the sequence of calls showing how the pattern works in the application.
- **.svg files** — versions that stay sharp when enlarged.
- **.puml files** — editable PlantUML sources.

| Folder | What it shows |
| --- | --- |
| Builder | Building a complete set of practice exercises through a client, director, and builder. |
| FactoryMethod | Creating multiple-choice or typing exercises through concrete factories. |
| Decorator | Wrapping an exercise with a hint and reducing the correct-answer reward to 5 XP. |
| Facade | Coordinating learning cards, practice, progress storage, and notifications through one application API. |
| Strategy | Choosing priority words with an interchangeable algorithm and filling the requested practice size. |
| Observer | Notifying the progress and achievement observers after a successful save. |

Builder and Factory Method create practice exercises. Learning cards use `StudySession` and do not produce scored answers. The Facade diagrams show this difference.

`Exercise` is shared between Factory Method and Decorator. It acts as the factory's Product and the decorator's Component, so there is no duplicate interface.

We left out test classes, input-parsing details, and less relevant methods to keep the diagrams focused. Role labels such as Client, Creator, and Concrete Strategy describe the existing classes; they are not additional classes.

In the class diagrams, a hollow triangle represents inheritance or interface implementation, a hollow diamond represents aggregation, and a filled diamond represents composition. Member visibility uses `+` for public, `-` for private, `#` for protected, and `~` for package access.
