import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class CharacterizationTest {

    @Test
    void cardColorIsDetected() {
        assertEquals("R", CardRules.color("R5"));
        assertEquals("Y", CardRules.color("Y2"));
        assertEquals("", CardRules.color("W"));
    }

    @Test
    void cardRankIsDetected() {
        assertEquals("NUMBER", CardRules.rank("R5"));
        assertEquals("SKIP", CardRules.rank("YS"));
        assertEquals("REVERSE", CardRules.rank("BR"));
        assertEquals("DRAW_TWO", CardRules.rank("G+2"));
        assertEquals("WILD", CardRules.rank("W"));
        assertEquals("WILD_DRAW_FOUR", CardRules.rank("W4"));
    }

    @Test
    void cardPointsAreCalculated() {
        assertEquals(5, CardRules.points("R5"));
        assertEquals(20, CardRules.points("YS"));
        assertEquals(20, CardRules.points("BR"));
        assertEquals(20, CardRules.points("G+2"));
        assertEquals(50, CardRules.points("W"));
        assertEquals(50, CardRules.points("W4"));
    }

    @Test
    void legalCardsCanMatchByColorNumberOrAction() {
        assertTrue(CardRules.isLegal("R9", "R5", ""));
        assertTrue(CardRules.isLegal("G5", "R5", ""));
        assertTrue(CardRules.isLegal("BS", "RS", ""));
        assertTrue(CardRules.isLegal("YR", "BR", ""));
        assertTrue(CardRules.isLegal("B+2", "G+2", ""));
    }

    @Test
    void wildCardsAreAlwaysLegal() {
        assertTrue(CardRules.isLegal("W", "R5", ""));
        assertTrue(CardRules.isLegal("W4", "R5", ""));
    }

    @Test
    void calledColorControlsAfterWild() {
        assertTrue(CardRules.isLegal("B3", "W", "B"));
        assertFalse(CardRules.isLegal("R3", "W", "B"));
    }

    @Test
    void mismatchedCardIsIllegal() {
        assertFalse(CardRules.isLegal("B3", "R5", ""));
    }

    @Test
    void botChoosesPlayableCardBeforeWild() {
        ArrayList<String> hand = new ArrayList<String>();
        hand.add("B3");
        hand.add("R4");
        hand.add("W");

        assertEquals(1, BotStrategy.chooseCard(hand, "R9", ""));
    }

    @Test
    void botChoosesMostCommonColor() {
        ArrayList<String> hand = new ArrayList<String>();
        hand.add("B1");
        hand.add("B2");
        hand.add("R3");

        assertEquals("B", BotStrategy.chooseColor(hand));
    }

    @Test
    void scoreCalculationUsesRemainingHands() {
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

        assertEquals(75, ScoreCalculator.calculateRemainingPoints(hands, 0));
    }

    @Test
    void nextPlayerMovesForwardAndWraps() {
        prepareThreePlayers();

        Main.currentPlayer = 0;
        Main.direction = 1;
        Main.next();

        assertEquals(1, Main.currentPlayer);

        Main.currentPlayer = 2;
        Main.next();

        assertEquals(0, Main.currentPlayer);
    }

    @Test
    void reverseChangesDirection() {
        prepareThreePlayers();

        Main.currentPlayer = 0;
        Main.direction = 1;

        Main.applyCardEffect("RR");

        assertEquals(-1, Main.direction);
        assertEquals(2, Main.currentPlayer);
    }

    @Test
    void skipSkipsOnePlayer() {
        prepareThreePlayers();

        Main.currentPlayer = 0;
        Main.direction = 1;

        Main.applyCardEffect("RS");

        assertEquals(2, Main.currentPlayer);
    }

    @Test
    void drawTwoAddsCardsAndSkipsPunishedPlayer() {
        prepareThreePlayers();

        Main.currentPlayer = 0;
        Main.direction = 1;

        Main.deck.clear();
        Main.discard.clear();
        Main.deck.add("R1");
        Main.deck.add("R2");

        Main.applyCardEffect("R+2");

        assertEquals(2, Main.hands.get(1).size());
        assertEquals(2, Main.currentPlayer);
    }

    @Test
    void emptyDeckAndDiscardFallbackReturnsWild() {
        Main.deck.clear();
        Main.discard.clear();

        assertEquals("W", Main.draw());
    }

    private void prepareThreePlayers() {
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
}
