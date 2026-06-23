import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScoringAndTargetTest {

    @Test
    void calculatesSingleHandPoints() {
        ArrayList<String> hand = new ArrayList<String>();

        hand.add("R5");   // 5
        hand.add("G0");   // 0
        hand.add("BS");   // 20
        hand.add("YR");   // 20
        hand.add("R+2");  // 20
        hand.add("W");    // 50
        hand.add("W4");   // 50

        assertEquals(165, ScoreCalculator.calculateHandPoints(hand));
    }

    @Test
    void calculatesRemainingPointsForRoundWinner() {
        ArrayList<ArrayList<String>> hands = new ArrayList<ArrayList<String>>();

        ArrayList<String> winner = new ArrayList<String>();

        ArrayList<String> loserOne = new ArrayList<String>();
        loserOne.add("R5");
        loserOne.add("GS");
        loserOne.add("W");

        ArrayList<String> loserTwo = new ArrayList<String>();
        loserTwo.add("B2");
        loserTwo.add("Y+2");

        hands.add(winner);
        hands.add(loserOne);
        hands.add(loserTwo);

        assertEquals(97, ScoreCalculator.calculateRemainingPoints(hands, 0));
    }

    @Test
    void detectsTargetScoreReached() {
        assertTrue(ScoreCalculator.hasReachedTarget(500, 500));
        assertTrue(ScoreCalculator.hasReachedTarget(520, 500));
    }

    @Test
    void findsHighestScorePlayerIndex() {
        int[] scores = new int[10];

        scores[0] = 120;
        scores[1] = 300;
        scores[2] = 180;

        assertEquals(1, ScoreCalculator.findHighestScoreIndex(scores, 3));
    }

    @Test
    void roundWinnerReceivesScoreInMainGameState() {
        Main.playerNames.clear();
        Main.humanPlayers.clear();
        Main.hands.clear();
        Main.deck.clear();
        Main.discard.clear();

        Main.playerNames.add("Bot1");
        Main.playerNames.add("Bot2");

        Main.humanPlayers.add(Boolean.FALSE);
        Main.humanPlayers.add(Boolean.FALSE);

        Main.hands.add(new ArrayList<String>());
        Main.hands.add(new ArrayList<String>());

        Main.currentPlayer = 0;
        Main.quiet = true;
        Main.resetScores();

        Main.hands.get(1).add("R5");
        Main.hands.get(1).add("W");

        Main.scoreWinner("Bot1");

        assertEquals(55, Main.scores[0]);
    }
}
