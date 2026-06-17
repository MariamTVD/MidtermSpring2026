import java.util.ArrayList;
import java.util.Scanner;
/** added imports */
import java.util.Collections;
import java.util.Random;


/** changed the Main class
    /**
 * Runnable CLI UNO-like game.
 *
 * Main keeps the game flow, while card rules, bot logic,
 * console prompting and scoring are extracted into helper classes.
 */


public class Main {

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
        int bots = 3;
        int games = 1;
        boolean human = false;
        long seed = System.currentTimeMillis();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--bots") && i + 1 < args.length) {
                bots = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--games") && i + 1 < args.length) {
                games = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--human")) {
                human = true;
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

        random = new Random(seed);
        setupPlayers(bots, human);

        if (playerNames.size() < 2 || playerNames.size() > 4) {
            System.out.println("UNO needs 2 to 4 players.");
            return;
        }

        for (int gameNumber = 1; gameNumber <= games; gameNumber++) {
            if (!quiet) {
                System.out.println();
                System.out.println("=== Game " + gameNumber + " ===");
            }

            playGame();
        }

        printFinalScores();
    }

    static void printHelp() {
        System.out.println("Usage:");
        System.out.println("java Main --bots 3 --games 5 --quiet");
        System.out.println("java Main --human --bots 2 --games 1");
        System.out.println("java Main --self-test");
    }

    static void setupPlayers(int bots, boolean human) {
        playerNames.clear();
        humanPlayers.clear();
        hands.clear();

        if (human) {
            playerNames.add("You");
            humanPlayers.add(Boolean.TRUE);
            hands.add(new ArrayList<String>());
        }

        for (int i = 1; i <= bots; i++) {
            playerNames.add("Bot" + i);
            humanPlayers.add(Boolean.FALSE);
            hands.add(new ArrayList<String>());
        }
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

        String[] colors = {"R", "Y", "G", "B"};

        for (String color : colors) {
            deck.add(color + "0");

            for (int n = 1; n <= 9; n++) {
                deck.add(color + n);
                deck.add(color + n);
            }

            deck.add(color + "S");
            deck.add(color + "S");

            deck.add(color + "R");
            deck.add(color + "R");

            deck.add(color + "+2");
            deck.add(color + "+2");
        }

        for (int i = 0; i < 4; i++) {
            deck.add("W");
            deck.add("W4");
        }

        Collections.shuffle(deck, random);
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

        printTurnInfo(name, hand);

        int chosenCardIndex = chooseCardForCurrentPlayer(hand);

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
            System.out.println(name + " hand: " + join(hand));
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

        if (!quiet) {
            System.out.println(name + " draws " + drawn);
        }

        if (CardRules.isLegal(drawn, upCard, calledColor)) {
            if (!humanPlayers.get(currentPlayer).booleanValue()) {
                return hand.size() - 1;
            }

            System.out.print("Play drawn card " + drawn + "? y/n: ");
            String answer = scanner.nextLine();

            if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
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
        if (!quiet) {
            System.out.println(name + " selected an invalid index and draws a penalty card.");
        }

        hand.add(draw());
        next();
    }

    static void giveIllegalCardPenalty(String name, ArrayList<String> hand, String card) {
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

        if (!quiet) {
            System.out.println(name + " plays " + card);
        }

        handleWildColorIfNeeded(name, hand, card);
        announceUnoIfNeeded(name, hand);

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

    static void announceUnoIfNeeded(String name, ArrayList<String> hand) {
        if (hand.size() == 1 && !quiet) {
            System.out.println(name + " says UNO!");
        }
    }

    static void scoreWinner(String name) {
        int points = ScoreCalculator.calculateRemainingPoints(hands, currentPlayer);
        scores[currentPlayer] += points;

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

            if (!quiet) {
                System.out.println(playerNames.get(currentPlayer) + " draws two.");
            }

            next();
        } else if (rank.equals("WILD_DRAW_FOUR")) {
            next();

            for (int i = 0; i < 4; i++) {
                hands.get(currentPlayer).add(draw());
            }

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

        System.out.println("Passed " + passed + " characterization checks.");
    }

    static void fail(String name) {
        throw new RuntimeException("Failed: " + name);
    }
}
