/** added a new test file to the code; */

import java.util.ArrayList;

/**
 * Executable characterization checks for the refactored version.
 *
 * These checks describe the current UNO implementation.
 * They are not trying to define perfect official UNO.
 */
public class CharacterizationTest {

    static int passed = 0;

    public static void main(String[] args) {
        testCardColor();
        testCardRank();
        testCardPoints();

        testLegalByColor();
        testLegalByNumber();
        testLegalByActionType();
        testWildCardsAreLegal();
        testCalledColorAfterWild();
        testIllegalMismatch();

        testBotChoosesPlayableCard();
        testBotChoosesMostCommonColor();

        testScoreCalculation();

        testNextPlayerForward();
        testNextPlayerWrapAround();
        testReverseChangesDirection();
        testSkipSkipsOnePlayer();
        testDrawTwoAddsCardsAndSkipsPlayer();
        testDrawFallbackWhenDeckAndDiscardAreEmpty();

        System.out.println("Passed " + passed + " characterization tests.");
    }

    static void testCardColor() {
        check(CardRules.color("R5").equals("R"), "R5 color is red");
        check(CardRules.color("Y2").equals("Y"), "Y2 color is yellow");
        check(CardRules.color("W").equals(""), "wild has no color");
    }

    static void testCardRank() {
        check(CardRules.rank("R5").equals("NUMBER"), "R5 is number");
        check(CardRules.rank("YS").equals("SKIP"), "YS is skip");
        check(CardRules.rank("BR").equals("REVERSE"), "BR is reverse");
        check(CardRules.rank("G+2").equals("DRAW_TWO"), "G+2 is draw two");
        check(CardRules.rank("W").equals("WILD"), "W is wild");
        check(CardRules.rank("W4").equals("WILD_DRAW_FOUR"), "W4 is wild draw four");
    }

    static void testCardPoints() {
        check(CardRules.points("R5") == 5, "number card points");
        check(CardRules.points("YS") == 20, "skip points");
        check(CardRules.points("BR") == 20, "reverse points");
        check(CardRules.points("G+2") == 20, "draw two points");
        check(CardRules.points("W") == 50, "wild points");
        check(CardRules.points("W4") == 50, "wild draw four points");
    }

    static void testLegalByColor() {
        check(CardRules.isLegal("R9", "R5", ""), "same color is legal");
    }

    static void testLegalByNumber() {
        check(CardRules.isLegal("G5", "R5", ""), "same number is legal");
    }

    static void testLegalByActionType() {
        check(CardRules.isLegal("BS", "RS", ""), "skip on skip is legal");
        check(CardRules.isLegal("YR", "BR", ""), "reverse on reverse is legal");
        check(CardRules.isLegal("B+2", "G+2", ""), "draw two on draw two is legal");
    }

    static void testWildCardsAreLegal() {
        check(CardRules.isLegal("W", "R5", ""), "wild is legal");
        check(CardRules.isLegal("W4", "R5", ""), "wild draw four is legal");
    }

    static void testCalledColorAfterWild() {
        check(CardRules.isLegal("B3", "W", "B"), "called blue allows blue card");
        check(!CardRules.isLegal("R3", "W", "B"), "called blue rejects red card");
    }

    static void testIllegalMismatch() {
        check(!CardRules.isLegal("B3", "R5", ""), "different color and number is illegal");
    }

    static void testBotChoosesPlayableCard() {
        ArrayList<String> hand = new ArrayList<String>();
        hand.add("B3");
        hand.add("R4");
        hand.add("W");

        check(BotStrategy.chooseCard(hand, "R9", "") == 1,
                "bot chooses playable red card before wild");
    }

    static void testBotChoosesMostCommonColor() {
        ArrayList<String> hand = new ArrayList<String>();
        hand.add("B1");
        hand.add("B2");
        hand.add("R3");

        check(BotStrategy.chooseColor(hand).equals("B"),
                "bot chooses most common color");
    }

    static void testScoreCalculation() {
        ArrayList<ArrayList<String>> hands = new ArrayList<ArrayList<String>>();

        ArrayList<String> winner = new ArrayList<String>();
        ArrayList<String> loserOne = new ArrayList<String>();
        ArrayList<String> loserTwo = new ArrayList<String>();

        loserOne.add("R5");
        loserOne.add("GS");

        loserTwo.add("W");

        hands.add(winner);
        hands.add(loserOne);
        hands.add(loserTwo);

        check(ScoreCalculator.calculateRemainingPoints(hands, 0) == 75,
                "winner receives points from other hands");
    }

    static void testNextPlayerForward() {
        prepareThreePlayers();

        Main.currentPlayer = 0;
        Main.direction = 1;

        Main.next();

        check(Main.currentPlayer == 1, "next moves forward");
    }

    static void testNextPlayerWrapAround() {
        prepareThreePlayers();

        Main.currentPlayer = 2;
        Main.direction = 1;

        Main.next();

        check(Main.currentPlayer == 0, "next wraps around");
    }

    static void testReverseChangesDirection() {
        prepareThreePlayers();

        Main.currentPlayer = 0;
        Main.direction = 1;

        Main.applyCardEffect("RR");

        check(Main.direction == -1, "reverse changes direction");
        check(Main.currentPlayer == 2, "reverse moves to previous player with three players");
    }

    static void testSkipSkipsOnePlayer() {
        prepareThreePlayers();

        Main.currentPlayer = 0;
        Main.direction = 1;

        Main.applyCardEffect("RS");

        check(Main.currentPlayer == 2, "skip skips next player");
    }

    static void testDrawTwoAddsCardsAndSkipsPlayer() {
        prepareThreePlayers();

        Main.currentPlayer = 0;
        Main.direction = 1;

        Main.deck.clear();
        Main.discard.clear();
        Main.deck.add("R1");
        Main.deck.add("R2");

        Main.applyCardEffect("R+2");

        check(Main.hands.get(1).size() == 2, "draw two adds two cards to next player");
        check(Main.currentPlayer == 2, "draw two skips punished player");
    }

    static void testDrawFallbackWhenDeckAndDiscardAreEmpty() {
        Main.deck.clear();
        Main.discard.clear();

        String card = Main.draw();

        check(card.equals("W"), "empty deck and discard fallback is W");
    }

    static void prepareThreePlayers() {
        Main.quiet = true;

        Main.playerNames.clear();
        Main.playerNames.add("Bot1");
        Main.playerNames.add("Bot2");
        Main.playerNames.add("Bot3");

        Main.humanPlayers.clear();
        Main.humanPlayers.add(Boolean.FALSE);
        Main.humanPlayers.add(Boolean.FALSE);
        Main.humanPlayers.add(Boolean.FALSE);

        Main.hands.clear();
        Main.hands.add(new ArrayList<String>());
        Main.hands.add(new ArrayList<String>());
        Main.hands.add(new ArrayList<String>());

        Main.deck.clear();
        Main.discard.clear();
    }

    static void check(boolean condition, String name) {
        if (!condition) {
            throw new RuntimeException("Failed: " + name);
        }

        passed++;
    }
}


