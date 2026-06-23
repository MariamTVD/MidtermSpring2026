import java.util.ArrayList;

/**
 * Calculates UNO scoring.
 *
 * Number cards are worth their face value.
 * Skip, Reverse, and Draw Two are worth 20.
 * Wild and Wild Draw Four are worth 50.
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

            points += calculateHandPoints(hands.get(i));
        }

        return points;
    }

    static int calculateHandPoints(ArrayList<String> hand) {
        int points = 0;

        for (String card : hand) {
            points += CardRules.points(card);
        }

        return points;
    }

    static boolean hasReachedTarget(int score, int targetScore) {
        return score >= targetScore;
    }

    static int findHighestScoreIndex(int[] scores, int playerCount) {
        int bestIndex = 0;

        for (int i = 1; i < playerCount; i++) {
            if (scores[i] > scores[bestIndex]) {
                bestIndex = i;
            }
        }

        return bestIndex;
    }
}
