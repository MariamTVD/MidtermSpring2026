import java.util.ArrayList;

/**
 * Encapsulates automated bot decisions.
 *
 * Existing behavior is intentionally preserved.
 */
public final class BotStrategy {
    private BotStrategy() {
    }

    static int selectPlayableCard(ArrayList<String> hand, String topCard, String activeColor) {
        int plusTwo = locateRank(hand, topCard, activeColor, "DRAW_TWO");
        if (plusTwo >= 0) {
            return plusTwo;
        }

        int skipCard = locateRank(hand, topCard, activeColor, "SKIP");
        if (skipCard >= 0) {
            return skipCard;
        }

        int numeric = locateRank(hand, topCard, activeColor, "NUMBER");
        if (numeric >= 0) {
            return numeric;
        }

        for (int i = 0; i < hand.size(); i++) {
            String card = hand.get(i);
            if (card.startsWith("W")) {
                return i;
            }
        }

        return -1;
    }

    private static int locateRank(ArrayList<String> hand,
                                  String topCard,
                                  String activeColor,
                                  String desiredRank) {
        for (int i = 0; i < hand.size(); i++) {
            String current = hand.get(i);

            if (CardRules.rank(current).equals(desiredRank)
                    && CardRules.isLegal(current, topCard, activeColor)) {
                return i;
            }
        }

        return -1;
    }

    static String selectColor(ArrayList<String> hand) {
        int red = 0;
        int yellow = 0;
        int green = 0;
        int blue = 0;

        for (String card : hand) {
            String color = CardRules.color(card);

            switch (color) {
                case "R":
                    red++;
                    break;
                case "Y":
                    yellow++;
                    break;
                case "G":
                    green++;
                    break;
                case "B":
                    blue++;
                    break;
                default:
                    break;
            }
        }

        if (red >= yellow && red >= green && red >= blue) {
            return "R";
        }

        if (yellow >= red && yellow >= green && yellow >= blue) {
            return "Y";
        }

        if (green >= red && green >= yellow && green >= blue) {
            return "G";
        }

        return "B";
    }
}
