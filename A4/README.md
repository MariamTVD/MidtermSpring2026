# UNO CLI - Assignment 4

This is a refactored CLI UNO-like game.

## Project Location

This Assignment 4 project is located in the `A4` directory.

If you are starting from the repository root, first enter the A4 folder:

```bash
cd A4
```

All Maven and Docker commands below should be run from inside the `A4` directory.

## Assignment 4 Additions

Assignment 4 adds project infrastructure for the existing UNO project:

* Maven build support
* Maven-based test execution
* runnable jar packaging
* logging with `java.util.logging`
* Docker support
* documented build, test, package, and run commands

## Requirements

To run the project locally, install:

* Java 17
* Maven 3.9 or newer

Docker is only required for the Docker commands.

## Local Build

Compile the project with Maven:

```bash
mvn compile
```

## Local Test

Run the JUnit characterization tests through Maven:

```bash
mvn test
```

## Local Run

Run a quiet bot game:

```bash
mvn exec:java "-Dexec.args=--bots 3 --games 1 --quiet --seed 1"
```

Run an interactive human game:

```bash
mvn exec:java "-Dexec.args=--human --bots 2 --games 1"
```

Run the built-in self-test:

```bash
mvn exec:java "-Dexec.args=--self-test"
```

## Package Creation

Create a runnable jar:

```bash
mvn package
```

Run the packaged jar:

```bash
java -jar target/uno-cli-1.0-SNAPSHOT.jar --bots 3 --games 1 --quiet --seed 1
```

## Docker Build

Build the Docker image:

```bash
docker build -t uno-cli .
```

## Docker Run

Run the default bot game from Docker:

```bash
docker run --rm uno-cli
```

Run Docker with custom game arguments:

```bash
docker run --rm uno-cli --bots 3 --games 1 --quiet --seed 1
```

## Logging

The project uses `java.util.logging`.

The game logs important events such as:

* game session start
* player turn
* card played
* card drawn
* invalid input
* round end
* game session end

Logging does not replace the normal player-facing CLI output. The CLI still prints readable game output and final scores.

## Notes

The `--seed` option is used in some commands to make the bot game deterministic. For example, running with `--seed 1` produces the same shuffle and same result each time, which is useful for testing.

To run a different random game, remove the seed:

```bash
mvn exec:java "-Dexec.args=--bots 3 --games 1 --quiet"
```

or use a different seed:

```bash
mvn exec:java "-Dexec.args=--bots 3 --games 1 --quiet --seed 99"
```
