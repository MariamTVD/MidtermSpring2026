# UNO Final Project

This project is a command-line UNO-style game implemented in Java.

It continues the earlier UNO assignments and expands the project into a fuller, more playable product with improved rules, tests, documentation, and command-line usability.

## Project Features

Implemented features include:

* Correct 108-card UNO-style deck composition
* Legal play validation
* Skip cards
* Reverse cards
* Draw Two cards
* Wild cards
* Wild Draw Four cards
* Draw/pass behavior
* UNO call and missed-UNO penalty
* Round scoring
* Multi-round target-score mode
* Bot-only games
* One-human games
* Multiple-human local games
* JUnit tests for deck, rules, action cards, draw/pass, UNO penalty, and scoring
* Human players can enter custom names at game start 
* Invalid player-count setups are rejected before asking for names

## Project Structure

```text
UNO_Final_Project
├─ pom.xml
├─ Dockerfile
├─ README.md
├─ docs
│  ├─ rules-supported.md
│  └─ final-report.md
└─ src
   ├─ main
   │  └─ java
   │     ├─ Main.java
   │     ├─ CardRules.java
   │     ├─ DeckFactory.java
   │     ├─ ScoreCalculator.java
   │     ├─ BotStrategy.java
   │     └─ ConsolePrompter.java
   └─ test
      └─ java
         ├─ DeckFactoryTest.java
         ├─ CardRulesTest.java
         ├─ ActionCardTest.java
         ├─ DrawPassTest.java
         ├─ UnoCallPenaltyTest.java
         └─ ScoringAndTargetTest.java
```

## Requirements

* Java 17
* Maven 3.x

No external database or machine-specific setup is required.

## Build

From the project root, run:

```bash
mvn compile
```

## Run Tests

Run all JUnit tests:

```bash
mvn test
```

The tests cover:

* Deck composition
* Legal card validation
* Skip, Reverse, Draw Two, and Wild Draw Four behavior
* Draw/pass behavior
* UNO call and penalty behavior
* Scoring and target-score helpers

## Run The Game

### Bot-Only Game

Run a quiet bot-only game:

```bash
mvn compile exec:java "-Dexec.args=--humans 0 --bots 3 --games 1 --quiet --seed 1"
```

### One Human Against Bots

Run one human player against two bots:

```bash
mvn compile exec:java "-Dexec.args=--human --bots 2 --games 1"
```

This is the same as:

```bash
mvn compile exec:java "-Dexec.args=--humans 1 --bots 2 --games 1"
```

### Multiple Human Players

Run two human players and one bot:

```bash
mvn compile exec:java "-Dexec.args=--humans 2 --bots 1 --games 1"
```

For multiple human players, the CLI asks players to pass the computer before showing the current player's hand.
When human players are used, the game asks each human player to enter their name before the game begins. 
Those names are then used during turns, scoring, and final score output.

### Target Score Mode

Run until a player reaches a target score:

```bash
mvn compile exec:java "-Dexec.args=--humans 0 --bots 3 --target 500 --quiet --seed 1"
```

For quicker testing, use a smaller target:

```bash
mvn compile exec:java "-Dexec.args=--humans 0 --bots 3 --target 100 --quiet --seed 1"
```

## Command-Line Options

```text
--humans N     Number of human players
--human        Shortcut for --humans 1
--bots N       Number of bot players
--games N      Number of rounds/games to play
--target N     Play multiple rounds until a player reaches target score
--quiet        Reduce console output
--seed N       Use deterministic random seed
--self-test    Run built-in characterization checks
--help         Show help message
```

The game supports 2 to 4 total players.

## Card Format

Cards use a compact text format:

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

## Gameplay Notes

The game follows normal UNO-like behavior where reasonable for this course project:

* A card may be played if it matches color, number, or action type.
* Wild and Wild Draw Four cards may be played as wild cards.
* Wild cards allow the player or bot to choose the next active color.
* Skip skips the next player.
* Reverse changes direction.
* In a two-player game, Reverse is treated like Skip.
* Draw Two makes the next player draw two cards and lose their turn.
* Wild Draw Four makes the next player draw four cards and lose their turn.
* If a player cannot play, they draw one card.
* If the drawn card is legal, it may be played immediately.
* If a human reaches one card and does not call UNO, they draw two penalty cards.
* Bots automatically call UNO.

## Package

Create a runnable jar:

```bash
mvn package
```

Run the packaged jar:

```bash
java -jar target/uno-final-project-1.0-SNAPSHOT.jar --humans 0 --bots 3 --games 1 --quiet --seed 1
```

## Docker

Build the Docker image:

```bash
docker build -t uno-final-project .
```

Run the Docker image:

```bash
docker run --rm uno-final-project
```

## Documentation

Additional final-project documentation is located in:

```text
docs/rules-supported.md
docs/final-report.md
```

`rules-supported.md` lists implemented rules and simplifications.

`final-report.md` explains the rules, CLI usage, architecture, tests, and remaining limitations.
