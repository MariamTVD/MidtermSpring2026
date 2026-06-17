# Extension Readiness

## Best Supported Extension

The refactored design best supports adding a smarter bot strategy.

This extension is now easier because bot decision logic is located in `BotStrategy` instead of being mixed directly into the main game loop. A future change could improve how bots choose cards without changing the card legality rules or the console input code.

For example, a smarter bot could:

* prefer action cards at better times
* avoid wasting wild cards too early
* choose a color based on both its own hand and the next player's likely options
* play higher-value cards first near the end of a game

## Where The Change Would Be Implemented

Most of the change would be implemented in:

```text
src/BotStrategy.java
```

The method most likely to change is:

```text
chooseCard(...)
```

The color selection behavior would be changed in:

```text
chooseColor(...)
```

The bot would still use `CardRules.isLegal(...)` to check whether a card can be played. This means the smarter bot would not need to duplicate the rule logic.

## Why The Current Design Helps

The current design helps because card legality and bot choice are separated.

`CardRules` answers questions such as:

* What color is this card?
* What rank is this card?
* Is this card legal on the current up card?
* How many points is this card worth?

`BotStrategy` answers a different question:

* Which legal card should the bot choose?

Because these responsibilities are separated, a bot improvement can focus on decision-making instead of reimplementing UNO rules.

## What Still Makes Change Difficult

The biggest remaining difficulty is that the game state is still stored in static fields inside `Main`.

For example, player hands, the deck, the discard pile, the current player, direction, up card, and called color are all stored globally in `Main`. This makes it harder to create isolated tests for full game situations.

Another difficulty is that cards are still represented as strings. This is compatible with the original implementation, but future extensions would be safer with a dedicated `Card` class or enum-based card type.

A future refactoring could introduce a `GameState` class to hold the state of the game. After that, card effects such as skip, reverse, draw two, and wild draw four could be moved into a separate rule-effect component.
