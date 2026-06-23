# Rules Supported

This document lists the UNO-style rules implemented in the final project and the simplifications used.

## Card Format

Cards are represented using compact string codes.

```text
R = Red
Y = Yellow
G = Green
B = Blue
```

Examples:

```text
R5   Red 5
YS   Yellow Skip
BR   Blue Reverse
G+2  Green Draw Two
W    Wild
W4   Wild Draw Four
```

## Implemented Rules

### Correct Deck Composition

Implemented.

The deck contains 108 cards:

* Four colors: red, yellow, green, blue
* One `0` card in each color
* Two cards for each number `1-9` in each color
* Two Skip cards in each color
* Two Reverse cards in each color
* Two Draw Two cards in each color
* Four Wild cards
* Four Wild Draw Four cards

The deck creation logic is located in:

```text
src/main/java/DeckFactory.java
```

Deck composition is tested in:

```text
src/test/java/DeckFactoryTest.java
```

### Legal Play Validation

Implemented.

A card is legal when at least one of the following is true:

* The card color matches the active color.
* The card number matches the top card number.
* The card action type matches the top card action type.
* The card is a Wild card.
* The card is a Wild Draw Four card.

After a Wild or Wild Draw Four is played, the selected called color becomes the active color.

Legal play logic is located in:

```text
src/main/java/CardRules.java
```

Legal play validation is tested in:

```text
src/test/java/CardRulesTest.java
```

### Skip

Implemented.

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

Implemented.

When a Reverse card is played, the direction of play changes.

In a two-player game, Reverse is treated like Skip. This means the same player effectively gets another turn after reversing.

This behavior is handled in:

```text
src/main/java/Main.java
```

and tested in:

```text
src/test/java/ActionCardTest.java
```

### Draw Two

Implemented.

When a Draw Two card is played:

* The next player draws two cards.
* The next player loses their turn.
* Play continues with the following player.

This behavior is handled in:

```text
src/main/java/Main.java
```

and tested in:

```text
src/test/java/ActionCardTest.java
```

### Wild

Implemented.

When a Wild card is played:

* The player chooses the next active color.
* For bot players, the bot chooses the color it has most often in its hand.
* The next player takes a normal turn.

Wild color behavior is handled in:

```text
src/main/java/Main.java
src/main/java/BotStrategy.java
src/main/java/ConsolePrompter.java
```

and tested in:

```text
src/test/java/CardRulesTest.java
```

### Wild Draw Four

Implemented.

When a Wild Draw Four card is played:

* The player chooses the next active color.
* The next player draws four cards.
* The next player loses their turn.
* Play continues with the following player.

This behavior is handled in:

```text
src/main/java/Main.java
```

and tested in:

```text
src/test/java/ActionCardTest.java
```

### Draw And Pass Behavior

Implemented.

This project uses the following draw/pass variant:

* If a player cannot or does not play a card, they draw one card.
* If the drawn card is legal, it may be played immediately.
* If the drawn card is not played, the turn passes.

For bots, a drawn legal card is played automatically.

For humans, the player is asked whether they want to play the drawn legal card.

This behavior is handled in:

```text
src/main/java/Main.java
src/main/java/ConsolePrompter.java
```

and tested in:

```text
src/test/java/DrawPassTest.java
```

### UNO Call And Missed-UNO Penalty

Implemented with a simple timing rule.

When a human player plays a card and has exactly one card left, the game asks the player to type `UNO`.

* If the player types `UNO`, no penalty is applied.
* If the player does not type `UNO`, the player draws two penalty cards.
* Bots automatically call UNO and do not receive the missed-UNO penalty.

This behavior is handled in:

```text
src/main/java/Main.java
src/main/java/ConsolePrompter.java
```

and tested in:

```text
src/test/java/UnoCallPenaltyTest.java
```

### Round Scoring

Implemented.

When a player empties their hand, that player wins the round.

The round winner receives points equal to the value of all cards remaining in the other players' hands.

Card values:

* Number cards: face value
* Skip: 20
* Reverse: 20
* Draw Two: 20
* Wild: 50
* Wild Draw Four: 50

Scoring logic is located in:

```text
src/main/java/ScoreCalculator.java
```

and tested in:

```text
src/test/java/ScoringAndTargetTest.java
```

### Multi-Round Target Score

Implemented.

The game supports target-score mode with:

```text
--target N
```

Example:

```bash
mvn compile exec:java "-Dexec.args=--humans 0 --bots 3 --target 500 --quiet --seed 1"
```

The game continues until a player reaches or exceeds the target score.

This behavior is handled in:

```text
src/main/java/Main.java
src/main/java/ScoreCalculator.java
```

and tested in:

```text
src/test/java/ScoringAndTargetTest.java
```

## CLI Playability

Implemented.

The game supports:

* Bot-only games
* One-human games
* Multiple-human local games

```text
When multiple human players are used, the game first asks each human player for their name. 
The game then uses those names in turn prompts, played-card messages, scoring, and final score output.
```

* Quiet mode for automated runs
* Seeded deterministic runs
* Target-score mode
* Help command
* Custom names for human players 
* Early validation for invalid total player counts

Example commands:

```bash
mvn compile exec:java "-Dexec.args=--humans 0 --bots 3 --games 1 --quiet --seed 1"
```

```bash
mvn compile exec:java "-Dexec.args=--humans 2 --bots 1 --games 1"
```

```bash
mvn compile exec:java "-Dexec.args=--humans 1 --bots 2 --target 500"
```

## Simplifications And Variants

The project intentionally uses the following simplifications:

### No Wild Draw Four Challenge Rule

Wild Draw Four challenge rules are not implemented.

Wild Draw Four always makes the next player draw four cards and lose their turn.

### No Draw Card Stacking

Draw Two and Wild Draw Four stacking are not implemented.

A Draw Two always makes the next player draw two and lose their turn.

A Wild Draw Four always makes the next player draw four and lose their turn.

### Two-Player Reverse

In a two-player game, Reverse is treated like Skip.

### Starting Action Card

If the first drawn top card is a Wild or Wild Draw Four, it is discarded and another starting card is drawn.

Other starting action cards are allowed as the top card, but their effect is not automatically applied at setup.

### Simple Bot Strategy

Bots use a simple strategy:

* Prefer Draw Two
* Prefer Skip
* Prefer Wild Draw Four
* Play a legal number card
* Play Reverse
* Play Wild
* Draw if no legal card exists

When choosing a Wild color, bots choose the color they have most often in their current hand.

### Text-Only Local Play

The game is a text-only command-line game.

For multiple human players, the game asks players to pass the computer before showing the next player's hand.
