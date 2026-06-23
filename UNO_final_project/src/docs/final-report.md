# Final Project Report

## Project Overview

This final project is a command-line UNO-style game written in Java.

The project continues the earlier UNO assignments and expands the game into a more complete product. The final version focuses on fuller UNO rules, better command-line playability, improved rule organization, and stronger automated tests.

The game supports bot-only games, one-human games, and multiple-human local games.

## Implemented UNO Rules

### Correct Deck Composition

The project implements a 108-card UNO-style deck.

The deck contains:

* Four colors: red, yellow, green, blue
* One `0` card in each color
* Two cards for each number `1-9` in each color
* Two Skip cards in each color
* Two Reverse cards in each color
* Two Draw Two cards in each color
* Four Wild cards
* Four Wild Draw Four cards

Deck creation is handled in:

```text
src/main/java/DeckFactory.java
```

The deck is tested in:

```text
src/test/java/DeckFactoryTest.java
```

### Legal Play Validation

The project implements legal play validation.

A card may be played if:

* It matches the current active color.
* It matches the top card number.
* It matches the top card action type.
* It is a Wild card.
* It is a Wild Draw Four card.

After a Wild or Wild Draw Four card is played, the selected color becomes the active color.

Legal play validation is handled in:

```text
src/main/java/CardRules.java
```

and tested in:

```text
src/test/java/CardRulesTest.java
```

### Skip

Skip cards are implemented.

When a Skip card is played, the next player loses their turn.

This behavior is handled in:

```text
src/main/java/Main.java
```

and tested in:

```text
src/test/java/ActionCardTest.java
```

### Reverse

Reverse cards are implemented.

When a Reverse card is played, the direction of play changes.

In a two-player game, Reverse is treated like Skip. This behavior is documented as a simplification.

Reverse behavior is tested in:

```text
src/test/java/ActionCardTest.java
```

### Draw Two

Draw Two cards are implemented.

When a Draw Two card is played:

* The next player draws two cards.
* The next player loses their turn.
* Play continues with the following player.

Draw Two behavior is tested in:

```text
src/test/java/ActionCardTest.java
```

### Wild

Wild cards are implemented.

When a Wild card is played:

* A human player chooses the next active color.
* A bot chooses the color it has most often in its hand.
* The next player takes a normal turn.

Wild color logic uses:

```text
src/main/java/Main.java
src/main/java/BotStrategy.java
src/main/java/ConsolePrompter.java
```

### Wild Draw Four

Wild Draw Four cards are implemented.

When a Wild Draw Four card is played:

* The player chooses the next active color.
* The next player draws four cards.
* The next player loses their turn.
* Play continues with the following player.

Wild Draw Four behavior is tested in:

```text
src/test/java/ActionCardTest.java
```

### Draw And Pass

The project implements a draw/pass flow.

The selected variant is:

* A player may draw one card.
* If the drawn card is legal, it may be played immediately.
* If the drawn card is not played, the turn passes.

For bots, a drawn legal card is played automatically.

For humans, the game asks whether to play the drawn legal card.

Draw/pass behavior is tested in:

```text
src/test/java/DrawPassTest.java
```

### UNO Call And Missed-UNO Penalty

The project implements a simple UNO call rule.

When a human player plays a card and has exactly one card left, the game asks the player to type `UNO`.

* If the player types `UNO`, no penalty is applied.
* If the player does not type `UNO`, the player draws two penalty cards.
* Bots automatically call UNO.

UNO behavior is handled in:

```text
src/main/java/Main.java
src/main/java/ConsolePrompter.java
```

and tested in:

```text
src/test/java/UnoCallPenaltyTest.java
```

### Round Scoring

Round scoring is implemented.

When a player empties their hand, that player wins the round. The round winner receives points equal to all remaining cards in the other players' hands.

Card values:

* Number cards: face value
* Skip: 20
* Reverse: 20
* Draw Two: 20
* Wild: 50
* Wild Draw Four: 50

Scoring is handled in:

```text
src/main/java/ScoreCalculator.java
```

and tested in:

```text
src/test/java/ScoringAndTargetTest.java
```

### Multi-Round Target Score

The game supports target-score mode with:

```bash
--target N
```

Example:

```bash
mvn compile exec:java "-Dexec.args=--humans 0 --bots 3 --target 500 --quiet --seed 1"
```

The game continues until one player reaches or exceeds the target score.

Target-score helper behavior is tested in:

```text
src/test/java/ScoringAndTargetTest.java
```

## CLI Usage

The game can be run from the command line using Maven.

### Run Tests

```bash
mvn test
```

### Bot-Only Game

```bash
mvn compile exec:java "-Dexec.args=--humans 0 --bots 3 --games 1 --quiet --seed 1"
```

### One Human Against Bots

```bash
mvn compile exec:java "-Dexec.args=--human --bots 2 --games 1"
```

or:

```bash
mvn compile exec:java "-Dexec.args=--humans 1 --bots 2 --games 1"
```

### Multiple Human Players

```bash
mvn compile exec:java "-Dexec.args=--humans 2 --bots 1 --games 1"
```
For multiple human players, the game asks the players to pass the computer before showing the next player's hand.
For multiple human players, the game asks each human player to enter their name before the game begins. 
These names are used in turn messages, card-play messages, scoring, and final score output.

Before asking for names, the game validates the total number of players. 
If the selected number of humans and bots is less than 2 or greater than 4, the game immediately prints an error and exits.

### Target Score Game

```bash
mvn compile exec:java "-Dexec.args=--humans 0 --bots 3 --target 500 --quiet --seed 1"
```

For quick testing:

```bash
mvn compile exec:java "-Dexec.args=--humans 0 --bots 3 --target 100 --quiet --seed 1"
```

### Help

```bash
mvn compile exec:java "-Dexec.args=--help"
```

## Architecture

The project separates the main parts of the game into helper classes instead of keeping everything inside console input code.

### Main.java

`Main.java` runs the command-line game flow.
It also validates the total player count before asking for human player names.

It handles:

* command-line options
* player setup
* round loop
* turn loop
* applying card effects
* target-score mode
* final score printing

### CardRules.java

`CardRules.java` contains card-related rule logic.

It handles:

* color extraction
* rank extraction
* number extraction
* legal play validation
* card point values
* valid card checking
* valid color checking

This makes card legality testable without console input.

### DeckFactory.java

`DeckFactory.java` creates the UNO deck.

It handles:

* 108-card deck creation
* shuffled deck creation
* card counting helpers for tests

This makes deck composition easy to test.

### ScoreCalculator.java

`ScoreCalculator.java` handles scoring.

It calculates:

* points in one hand
* remaining points after a round
* target-score checks
* highest-score player index

### BotStrategy.java

`BotStrategy.java` controls bot decisions.

Bots use a simple but playable strategy:

* prefer Draw Two
* prefer Skip
* prefer Wild Draw Four
* play legal number cards
* play Reverse
* play Wild
* draw if no legal card exists

Bots choose Wild colors based on the most common color in their hand.

### ConsolePrompter.java

`ConsolePrompter.java` handles human input.

It supports:

* choosing a card by index
* choosing a card by code
* drawing
* viewing the hand
* help command
* quitting
* choosing Wild colors
* calling UNO
* multiple-human turn handoff prompts
* It also supports clearer local multiplayer by waiting before showing each human player's hand.


This keeps console input separate from most rule logic.

## Tests Added

The final project includes the following test files.

### DeckFactoryTest.java

Tests:

* 108-card deck size
* correct color card counts
* correct number card counts
* correct action card counts
* correct Wild and Wild Draw Four counts

### CardRulesTest.java

Tests:

* color extraction
* rank extraction
* number extraction
* same-color legal play
* same-number legal play
* same-action legal play
* Wild and Wild Draw Four legality
* illegal move rejection
* called-color behavior after Wild cards
* point values
* valid/invalid card format

### ActionCardTest.java

Tests:

* Skip skips the next player
* Reverse changes direction
* Reverse acts like Skip in two-player games
* Draw Two makes the next player draw two and lose turn
* Wild Draw Four makes the next player draw four and lose turn
* normal cards move to the next player

### DrawPassTest.java

Tests:

* bot draws when no legal card exists
* bot can play a drawn card if it is legal
* called color is respected after Wild cards
* passing moves to the next player

### UnoCallPenaltyTest.java

Tests:

* bot with one card left does not receive a penalty
* no UNO check occurs when a player has more than one card
* playing down to one card triggers UNO handling
* missed-UNO penalty behavior is represented

### ScoringAndTargetTest.java

Tests:

* single-hand point calculation
* remaining-card scoring
* target-score detection
* highest-score player detection
* round winner receives score in the main game state

## Limitations And Simplifications

The project intentionally keeps several rules simple.

### No Wild Draw Four Challenge

Wild Draw Four challenge rules are not implemented.

Wild Draw Four always makes the next player draw four cards and lose their turn.

### No Draw Stacking

Draw Two and Wild Draw Four stacking are not implemented.

### Two-Player Reverse Variant

In a two-player game, Reverse is treated like Skip.

### Starting Action Cards

If the starting top card is Wild or Wild Draw Four, the game discards it and draws another starting card.

Other action cards may appear as the starting top card, but their effect is not automatically applied at setup.

### Simple Bot Strategy

Bots are functional but not advanced. They follow a simple priority order and do not use complex strategy.

### Text-Only Interface

The game is a command-line game. It does not include a graphical interface.

## Final Verification

The following commands were used to verify the project:

```bash
mvn test
```

```bash
mvn compile exec:java "-Dexec.args=--self-test"
```

```bash
mvn compile exec:java "-Dexec.args=--humans 0 --bots 3 --games 1 --quiet --seed 1"
```

```bash
mvn compile exec:java "-Dexec.args=--humans 0 --bots 3 --target 100 --quiet --seed 1"
```

All tests and verification commands completed successfully during development.
