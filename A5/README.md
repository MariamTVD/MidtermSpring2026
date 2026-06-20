# UNO CLI - Assignment 5

This Assignment 5 project is located in the `A5` directory.

If you are starting from the repository root, first enter the A5 folder:

```bash
cd A5
```

All Maven commands below should be run from inside the `A5` directory.

## Assignment 5 Goal

Assignment 5 adds persistence to the UNO CLI project.

The game stores completed game history and supports report commands for player statistics.

## Persistence Technology

This project uses:

- MyBatis as the persistence mapper framework
- H2 as the local development database
- SQL schema stored in `src/main/resources/schema.sql`
- MyBatis mapper XML stored in `src/main/resources/mappers/GameHistoryMapper.xml`

The H2 database is created locally as:

```text
uno-history.mv.db
```

This database file is generated when the game or report commands run.

## Schema

The schema supports:

- players
- games
- rounds
- player scores
- winners
- timestamps

Main tables:

```text
players
games
rounds
player_scores
```

## Build

Compile the project:

```bash
mvn compile
```

## Test

Run tests:

```bash
mvn test
```

## Run A Game And Save History

Run a quiet bot game:

```bash
mvn compile exec:java "-Dexec.args=--bots 3 --games 1 --quiet --seed 1"
```

When a game completes, the result is persisted to the H2 database.

Persisted data includes:

- player names
- game completion timestamp
- round information
- per-player final scores
- final winner

## Report Commands

Show recent games:

```bash
mvn compile exec:java "-Dexec.args=--history"
```

Show player win counts:

```bash
mvn compile exec:java "-Dexec.args=--wins"
```

Show highest scores:

```bash
mvn compile exec:java "-Dexec.args=--high-scores"
```

## Package

Create a runnable jar:

```bash
mvn package
```

Run the packaged jar:

```bash
java -jar target/uno-cli-1.0-SNAPSHOT.jar --bots 3 --games 1 --quiet --seed 1
```

## Docker

Build the Docker image:

```bash
docker build -t uno-cli-a5 .
```

Run the Docker image:

```bash
docker run --rm uno-cli-a5
```

## Notes

The project uses a local H2 database for development and testing.

No private database credentials are required. The database is created from repository files and local runtime state.

When running through Maven `exec:java`, H2 may print shutdown/thread warnings in some environments. The report commands still complete successfully with `BUILD SUCCESS`.
