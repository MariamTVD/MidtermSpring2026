import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Runnable CLI UNO-like game.
 *
 * Main keeps the game flow, while card rules, bot logic,
 * console prompting, and scoring are extracted into helper classes.
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    static ArrayList<String> playerNames = new ArrayList<String>();
    static ArrayList<Boolean> humanPlayers = new ArrayList<Boolean>();
    static ArrayList<ArrayList<String>> hands = new ArrayList<ArrayList<String>>();

    static ArrayList<String> deck = new ArrayList<String>();
    static ArrayList<String> discard = new ArrayList<String>();

    static int[] scores = new int[10];

    static int currentPlayer = 0;
    static int direction = 1;

    static String upCard = "";
    static String calledColor = "";

    static boolean quiet = false;

    static Random random = new Random();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        configureLogging();

        int humans = 0;
        int bots = 3;
        int games = 1;
        int targetScore = 500;
        boolean useTargetScore = false;
        long seed = System.currentTimeMillis();

        LOGGER.info("UNO application started.");

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--humans") && i + 1 < args.length) {
                humans = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--human")) {
                humans = 1;
            } else if (args[i].equals("--bots") && i + 1 < args.length) {
                bots = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--games") && i + 1 < args.length) {
                games = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--target") && i + 1 < args.length) {
                targetScore = Integer.parseInt(args[++i]);
                useTargetScore = true;
            } else if (args[i].equals("--quiet")) {
                quiet = true;
            } else if (args[i].equals("--seed") && i + 1 < args.length) {
                seed = Long.parseLong(args[++i]);
            } else if (args[i].equals("--self-test")) {
                selfTest();
                return;
            } else if (args[i].equals("--help")) {
                printHelp();
                return;
            }
        }

        if (humans < 0 || bots < 0 || games < 1 || targetScore < 1) {
            System.out.println("Invalid arguments.");
            System.out.println("Humans and bots cannot be negative.");
            System.out.println("Games and target score must be at least 1.");
            return;
        }

        int totalPlayers = humans + bots;

        if (totalPlayers < 2 || totalPlayers > 4) {
            System.out.println("UNO needs 2 to 4 total players.");
            System.out.println("You selected " + humans + " human player(s) and " + bots + " bot player(s).");
            System.out.println("Example: --humans 2 --bots 1");
            return;
        }

        random = new Random(seed);
        setupPlayers(humans, bots);

        resetScores();

        LOGGER.info(
                "Game session started with "
                        + humans + " human player(s), "
                        + bots + " bot player(s), and "
                        + games + " game(s)."
        );

        int gameNumber = 1;
        int safetyRoundLimit = 100;

        while (gameNumber <= safetyRoundLimit) {
            if (!quiet) {
                System.out.println();
                System.out.println("=== Game " + gameNumber + " ===");
            }

            LOGGER.info("Round " + gameNumber + " started.");
            playGame();
            LOGGER.info("Round " + gameNumber + " ended.");

            if (useTargetScore && hasAnyPlayerReachedTarget(targetScore)) {
                printTargetWinner(targetScore);
                break;
            }

            if (!useTargetScore && gameNumber >= games) {
                break;
            }

            gameNumber++;
        }

        if (gameNumber >= safetyRoundLimit && !hasAnyPlayerReachedTarget(targetScore)) {
            System.out.println("Stopped after safety round limit.");
        }

        LOGGER.info("Game session ended.");
        printFinalScores();
    }

    static void configureLogging() {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.INFO);

        for (java.util.logging.Handler handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);

        consoleHandler.setFormatter(new java.util.logging.Formatter() {
            @Override
            public String format(LogRecord record) {
                return "[LOG] " + record.getMessage() + System.lineSeparator();
            }
        });

        rootLogger.addHandler(consoleHandler);
        LOGGER.setLevel(Level.INFO);
    }

    static void printHelp() {
        System.out.println("Usage:");
        System.out.println("java Main --humans 0 --bots 3 --games 5 --quiet");
        System.out.println("java Main --humans 2 --bots 1 --games 1");
        System.out.println("java Main --humans 1 --bots 2 --target 500");
        System.out.println("java Main --human --bots 2 --games 1");
        System.out.println("java Main --self-test");
        System.out.println();
        System.out.println("Options:");
        System.out.println("--humans N     Number of human players, from 0 to 4");
        System.out.println("--human        Shortcut for --humans 1");
        System.out.println("--bots N       Number of bot players");
        System.out.println("--games N      Number of rounds/games to play");
        System.out.println("--target N     Play multiple rounds until a player reaches target score");
        System.out.println("--quiet        Reduce console output");
        System.out.println("--seed N       Use deterministic random seed");
        System.out.println("--self-test    Run built-in characterization checks");
        System.out.println("--help         Show this help message");
    }

    static void setupPlayers(int humans, int bots) {
        playerNames.clear();
        humanPlayers.clear();
        hands.clear();

        for (int i = 1; i <= humans; i++) {
            String playerName = askHumanPlayerName(i);

            playerNames.add(playerName);
            humanPlayers.add(Boolean.TRUE);
            hands.add(new ArrayList<String>());
        }

        for (int i = 1; i <= bots; i++) {
            playerNames.add("Bot" + i);
            humanPlayers.add(Boolean.FALSE);
            hands.add(new ArrayList<String>());
        }
    }

    static String askHumanPlayerName(int playerNumber) {
        if (quiet) {
            return "Human" + playerNumber;
        }

        while (true) {
            System.out.print("Enter name for player " + playerNumber + ": ");

            String name = scanner.nextLine().trim();

            if (!name.equals("")) {
                return name;
            }

            System.out.println("Name cannot be empty.");
        }
    }

    static void resetScores() {
        for (int i = 0; i < scores.length; i++) {
            scores[i] = 0;
        }
    }

    static int countHumanPlayers() {
        int count = 0;

        for (Boolean humanPlayer : humanPlayers) {
            if (humanPlayer.booleanValue()) {
                count++;
            }
        }

        return count;
    }

    static void playGame() {
        setupDeck();
        dealCards();
        setupStartingUpCard();

        calledColor = "";
        direction = 1;
        currentPlayer = random.nextInt(playerNames.size());

        int guard = 0;

        while (guard < 3000) {
            guard++;

            playTurn();

            if (hasGameEnded()) {
                return;
            }
        }

        if (!quiet) {
            System.out.println("Game stopped at safety limit.");
        }
    }

    static void setupDeck() {
        deck.clear();
        discard.clear();
        deck.addAll(DeckFactory.createShuffledDeck(random));
    }

    static void dealCards() {
        for (ArrayList<String> hand : hands) {
            hand.clear();
        }

        for (int i = 0; i < playerNames.size(); i++) {
            for (int j = 0; j < 7; j++) {
                hands.get(i).add(draw());
            }
        }
    }

    static void setupStartingUpCard() {
        upCard = draw();

        while (upCard.startsWith("W")) {
            discard.add(upCard);
            upCard = draw();
        }
    }

    static void playTurn() {
        String name = playerNames.get(currentPlayer);
        ArrayList<String> hand = hands.get(currentPlayer);

        LOGGER.info("Player turn: " + name + ", up card: " + upCard + calledColorText());

        printTurnInfo(name, hand);

        int chosenCardIndex = chooseCardForCurrentPlayer(hand);

        if (chosenCardIndex == -99) {
            System.out.println("Game ended by player.");
            System.exit(0);
        }

        if (chosenCardIndex == -1) {
            chosenCardIndex = handleDrawChoice(name, hand);
        }

        if (chosenCardIndex >= 0) {
            playChosenCardOrPenalty(name, hand, chosenCardIndex);
        } else {
            next();
        }
    }

    static void printTurnInfo(String name, ArrayList<String> hand) {
        if (!quiet) {
            System.out.println();
            System.out.println("Up card: " + upCard + calledColorText());

            if (humanPlayers.get(currentPlayer).booleanValue()) {
                if (countHumanPlayers() > 1) {
                    ConsolePrompter.waitForHumanTurn(scanner, name);
                }

                System.out.println(name + " hand: " + join(hand));
            } else {
                System.out.println(name + " has " + hand.size() + " card(s).");
            }
        }
    }

    static String calledColorText() {
        if (calledColor.equals("")) {
            return "";
        }

        return " called " + calledColor;
    }

    static int chooseCardForCurrentPlayer(ArrayList<String> hand) {
        if (humanPlayers.get(currentPlayer).booleanValue()) {
            return ConsolePrompter.askHumanCardChoice(scanner, hand, upCard, calledColor);
        }

        return BotStrategy.chooseCard(hand, upCard, calledColor);
    }

    static int handleDrawChoice(String name, ArrayList<String> hand) {
        String drawn = draw();
        hand.add(drawn);

        LOGGER.info("Card drawn: " + name + " drew " + drawn);

        if (!quiet) {
            if (humanPlayers.get(currentPlayer).booleanValue()) {
                System.out.println(name + " draws " + drawn);
            } else {
                System.out.println(name + " draws a card.");
            }
        }

        if (CardRules.isLegal(drawn, upCard, calledColor)) {
            if (!humanPlayers.get(currentPlayer).booleanValue()) {
                return hand.size() - 1;
            }

            if (ConsolePrompter.askPlayDrawnCard(scanner, drawn)) {
                return hand.size() - 1;
            }
        }

        return -1;
    }

    static void playChosenCardOrPenalty(String name,
                                        ArrayList<String> hand,
                                        int chosenCardIndex) {
        if (chosenCardIndex >= hand.size()) {
            giveInvalidIndexPenalty(name, hand);
            return;
        }

        String card = hand.get(chosenCardIndex);

        if (!CardRules.isLegal(card, upCard, calledColor)) {
            giveIllegalCardPenalty(name, hand, card);
            return;
        }

        playLegalCard(name, hand, chosenCardIndex, card);
    }

    static void giveInvalidIndexPenalty(String name, ArrayList<String> hand) {
        LOGGER.warning("Invalid input: " + name + " selected an invalid index.");

        if (!quiet) {
            System.out.println(name + " selected an invalid index and draws a penalty card.");
        }

        hand.add(draw());
        next();
    }

    static void giveIllegalCardPenalty(String name, ArrayList<String> hand, String card) {
        LOGGER.warning("Invalid input: " + name + " tried illegal card " + card);

        if (!quiet) {
            System.out.println(name + " tried illegal card " + card + " and draws a penalty card.");
        }

        hand.add(draw());
        next();
    }

    static void playLegalCard(String name,
                              ArrayList<String> hand,
                              int chosenCardIndex,
                              String card) {
        hand.remove(chosenCardIndex);

        discard.add(upCard);
        upCard = card;
        calledColor = "";

        LOGGER.info("Card played: " + name + " played " + card);

        if (!quiet) {
            System.out.println(name + " plays " + card);
        }

        handleWildColorIfNeeded(name, hand, card);
        handleUnoCallIfNeeded(name, hand);

        if (hand.size() == 0) {
            scoreWinner(name);
            return;
        }

        applyCardEffect(card);
    }

    static void handleWildColorIfNeeded(String name,
                                        ArrayList<String> hand,
                                        String card) {
        if (card.equals("W") || card.equals("W4")) {
            if (humanPlayers.get(currentPlayer).booleanValue()) {
                calledColor = ConsolePrompter.askColor(scanner);
            } else {
                calledColor = BotStrategy.chooseColor(hand);
            }

            if (!quiet) {
                System.out.println(name + " calls " + calledColor);
            }
        }
    }

    static void handleUnoCallIfNeeded(String name, ArrayList<String> hand) {
        if (hand.size() != 1) {
            return;
        }

        if (humanPlayers.get(currentPlayer).booleanValue()) {
            boolean calledUno = ConsolePrompter.askUnoCall(scanner, name);

            if (calledUno) {
                if (!quiet) {
                    System.out.println(name + " says UNO!");
                }
            } else {
                hand.add(draw());
                hand.add(draw());

                LOGGER.info("UNO penalty: " + name + " missed UNO and drew two cards.");

                if (!quiet) {
                    System.out.println(name + " missed UNO and draws two penalty cards.");
                }
            }
        } else {
            if (!quiet) {
                System.out.println(name + " says UNO!");
            }
        }
    }

    static void scoreWinner(String name) {
        int points = ScoreCalculator.calculateRemainingPoints(hands, currentPlayer);
        scores[currentPlayer] += points;

        LOGGER.info("Round ended: " + name + " won and scored " + points + " points.");

        if (!quiet) {
            System.out.println(name + " wins and scores " + points);
        }
    }

    static void applyCardEffect(String card) {
        String rank = CardRules.rank(card);

        if (rank.equals("SKIP")) {
            next();
            next();
        } else if (rank.equals("REVERSE")) {
            direction = direction * -1;

            if (playerNames.size() == 2) {
                next();
                next();
            } else {
                next();
            }
        } else if (rank.equals("DRAW_TWO")) {
            next();

            hands.get(currentPlayer).add(draw());
            hands.get(currentPlayer).add(draw());

            LOGGER.info("Card drawn: " + playerNames.get(currentPlayer) + " drew two cards.");

            if (!quiet) {
                System.out.println(playerNames.get(currentPlayer) + " draws two.");
            }

            next();
        } else if (rank.equals("WILD_DRAW_FOUR")) {
            next();

            for (int i = 0; i < 4; i++) {
                hands.get(currentPlayer).add(draw());
            }

            LOGGER.info("Card drawn: " + playerNames.get(currentPlayer) + " drew four cards.");

            if (!quiet) {
                System.out.println(playerNames.get(currentPlayer) + " draws four.");
            }

            next();
        } else {
            next();
        }
    }

    static boolean hasGameEnded() {
        for (ArrayList<String> hand : hands) {
            if (hand.size() == 0) {
                return true;
            }
        }

        return false;
    }

    static String draw() {
        if (deck.size() == 0) {
            deck.addAll(discard);
            discard.clear();
            Collections.shuffle(deck, random);
        }

        if (deck.size() == 0) {
            return "W";
        }

        return deck.remove(0);
    }

    static void next() {
        currentPlayer += direction;

        if (currentPlayer >= playerNames.size()) {
            currentPlayer = 0;
        }

        if (currentPlayer < 0) {
            currentPlayer = playerNames.size() - 1;
        }
    }

    static boolean hasAnyPlayerReachedTarget(int targetScore) {
        for (int i = 0; i < playerNames.size(); i++) {
            if (ScoreCalculator.hasReachedTarget(scores[i], targetScore)) {
                return true;
            }
        }

        return false;
    }

    static void printTargetWinner(int targetScore) {
        int winnerIndex = ScoreCalculator.findHighestScoreIndex(scores, playerNames.size());

        System.out.println();
        System.out.println("Target score reached: " + targetScore);
        System.out.println("Match winner: " + playerNames.get(winnerIndex)
                + " with " + scores[winnerIndex] + " points.");
    }

    static String join(ArrayList<String> cards) {
        String output = "";

        for (int i = 0; i < cards.size(); i++) {
            output += i + ":" + cards.get(i);

            if (i < cards.size() - 1) {
                output += " ";
            }
        }

        return output;
    }

    static void printFinalScores() {
        System.out.println();
        System.out.println("Final scores:");

        for (int i = 0; i < playerNames.size(); i++) {
            System.out.println(playerNames.get(i) + ": " + scores[i]);
        }
    }

    static void selfTest() {
        int passed = 0;

        if (CardRules.color("R5").equals("R")) {
            passed++;
        } else {
            fail("color R5");
        }

        if (CardRules.rank("G+2").equals("DRAW_TWO")) {
            passed++;
        } else {
            fail("rank G+2");
        }

        if (CardRules.points("W4") == 50) {
            passed++;
        } else {
            fail("wild draw four points");
        }

        if (CardRules.isLegal("R2", "R9", "")) {
            passed++;
        } else {
            fail("same color legal");
        }

        if (CardRules.isLegal("G9", "R9", "")) {
            passed++;
        } else {
            fail("same number legal");
        }

        if (CardRules.isLegal("BS", "RS", "")) {
            passed++;
        } else {
            fail("same action legal");
        }

        if (CardRules.isLegal("W", "R9", "")) {
            passed++;
        } else {
            fail("wild legal");
        }

        if (CardRules.isLegal("W4", "R9", "")) {
            passed++;
        } else {
            fail("wild draw four legal");
        }

        if (CardRules.isLegal("B3", "W", "B")) {
            passed++;
        } else {
            fail("called color legal");
        }

        if (!CardRules.isLegal("B3", "R9", "")) {
            passed++;
        } else {
            fail("illegal mismatch");
        }

        ArrayList<String> hand = new ArrayList<String>();
        hand.add("B3");
        hand.add("R4");
        hand.add("W");

        if (BotStrategy.chooseCard(hand, "R9", "") == 1) {
            passed++;
        } else {
            fail("bot chooses legal normal card before wild");
        }

        ArrayList<String> colorHand = new ArrayList<String>();
        colorHand.add("B1");
        colorHand.add("B2");
        colorHand.add("R3");

        if (BotStrategy.chooseColor(colorHand).equals("B")) {
            passed++;
        } else {
            fail("bot chooses most common color");
        }

        ArrayList<ArrayList<String>> scoreHands = new ArrayList<ArrayList<String>>();

        ArrayList<String> winner = new ArrayList<String>();
        ArrayList<String> loser = new ArrayList<String>();
        loser.add("R5");
        loser.add("GS");
        loser.add("W");

        scoreHands.add(winner);
        scoreHands.add(loser);

        if (ScoreCalculator.calculateRemainingPoints(scoreHands, 0) == 75) {
            passed++;
        } else {
            fail("score calculation");
        }

        ArrayList<String> fullDeck = DeckFactory.createDeck();

        if (fullDeck.size() == 108) {
            passed++;
        } else {
            fail("deck size 108");
        }

        System.out.println("Passed " + passed + " characterization checks.");
    }

    static void fail(String name) {
        throw new RuntimeException("Failed: " + name);
    }
}

