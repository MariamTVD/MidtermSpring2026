import java.util.ArrayList;

/**
 * Computes points from remaining player hands.
 */
public final class ScoreCalculator {
    private ScoreCalculator() {
    }

    static int calculateRemainingPoints(ArrayList<ArrayList<String>> hands,
                                        int winnerIndex) {

        int total = 0;

        for (int i = 0; i < hands.size(); i++) {
            if (i == winnerIndex) {
                continue;
            }

            for (String card : hands.get(i)) {
                total += CardRules.points(card);
            }
        }

        return total;
    }
}
