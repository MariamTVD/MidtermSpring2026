/** updated ScoreCalculator */


import java.util.ArrayList;

/**
 * Calculates the winner's score from the remaining cards.
 */
public final class ScoreCalculator {

    private ScoreCalculator() {
    }

    static int calculateRemainingPoints(ArrayList<ArrayList<String>> hands,
                                        int winnerIndex) {
        int points = 0;

        for (int i = 0; i < hands.size(); i++) {
            if (i == winnerIndex) {
                continue;
            }

            for (String card : hands.get(i)) {
                points += CardRules.points(card);
            }
        }

        return points;
    }
}
