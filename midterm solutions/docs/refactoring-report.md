# Refactoring Report

## Behavior Characterized Before And During Refactoring

I characterized the existing UNO-like CLI behavior through executable checks in `Main --self-test` and in `CharacterizationTest`.

The checks cover the important rule behavior of the current implementation:

* matching a card by color
* matching a card by number
* matching action cards by action type
* allowing wild and wild draw four cards
* applying called color after a wild card
* rejecting illegal mismatches
* calculating card points
* calculating the winner's score from the remaining hands
* bot card choice
* bot color choice
* moving to the next player
* wrapping around the player list
* reverse behavior
* skip behavior
* draw two behavior
* fallback drawing behavior when the deck and discard pile are empty

These checks describe the behavior of this implementation rather than trying to define perfect official UNO rules.

## Worst Design Problems Found

The original design had too much responsibility concentrated in `Main`. The same class handled game flow, rule checking, scoring, bot decisions, console prompting, card interpretation, and turn effects.

The most important problems were:

* card rule logic was mixed with the game loop
* bot decisions depended directly on low-level card checks
* console input was mixed with game rule logic
* scoring was part of the main game flow
* the code was difficult to test without running the full CLI game
* the `Main` class had too many reasons to change

This made the game harder to safely modify because a change to one area, such as card legality, could accidentally affect unrelated behavior.

## Refactorings Performed

I refactored the project incrementally by extracting focused helper classes while preserving the existing CLI behavior.

The main extracted classes are:

* `CardRules`: contains card color, rank, number, legality, and point logic
* `BotStrategy`: contains automated bot card and color decisions
* `ConsolePrompter`: handles human console input and color selection
* `ScoreCalculator`: calculates the winner's score from remaining hands

`Main` remains responsible for running the game, managing turns, applying card effects, and coordinating the other classes. This keeps the application runnable while reducing the amount of rule and support logic inside the main game loop.

I also added executable characterization checks so that important behavior can be tested without manually playing a full game every time.

## Behavior Intentionally Preserved

I intentionally preserved the simplified behavior of the provided UNO implementation, including:

* all hands may be printed in the terminal
* humans may choose to draw even when they have a legal card
* wild and wild draw four cards are always legal
* after a wild card, the called color controls the next legal color
* bots automatically play a drawn card if it is legal
* skip skips the next player
* reverse changes direction
* with two players, reverse effectively gives the same player another turn
* draw two makes the next player draw two cards and lose their turn
* wild draw four makes the next player draw four cards and lose their turn
* the game stops at a safety limit if no player wins
* if the deck and discard pile are both empty, the draw method returns a fallback wild card

These behaviors were preserved because the goal was refactoring, not replacing the game with a different UNO implementation.

## Risks Remaining

The main remaining risk is that `Main` still contains a large amount of turn orchestration and card effect handling. The project is more modular than before, but the game state is still stored in static fields, which makes deeper testing and future extension harder.

Another risk is that cards are still represented as strings such as `R5`, `YS`, and `W4`. This keeps compatibility with the original implementation, but a stronger design would eventually introduce a real `Card` object.

The current refactoring improves separation of responsibilities, but the next step would be to move more game state and turn effect logic out of `Main`.
