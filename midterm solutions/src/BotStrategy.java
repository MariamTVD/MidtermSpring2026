/** updated BotStrategy code: */

import java.util.ArrayList;

/**
 * Bot decision logic.
 *
 * This keeps the automatic player behavior outside Main.
 */
public final class BotStrategy {

    private BotStrategy() {
    }

    static int chooseCard(ArrayList<String> hand, String upCard, String calledColor) {
        int drawTwo = findLegalCardByRank(hand, upCard, calledColor, "DRAW_TWO");
        if (drawTwo >= 0) {
            return drawTwo;
        }

        int skip = findLegalCardByRank(hand, upCard, calledColor, "SKIP");
        if (skip >= 0) {
            return skip;
        }

        int number = findLegalCardByRank(hand, upCard, calledColor, "NUMBER");
        if (number >= 0) {
            return number;
        }

        int reverse = findLegalCardByRank(hand, upCard, calledColor, "REVERSE");
        if (reverse >= 0) {
            return reverse;
        }

        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).startsWith("W")) {
                return i;
            }
        }

        return -1;
    }

    private static int findLegalCardByRank(ArrayList<String> hand,
                                           String upCard,
                                           String calledColor,
                                           String wantedRank) {
        for (int i = 0; i < hand.size(); i++) {
            String card = hand.get(i);

            if (CardRules.rank(card).equals(wantedRank)
                    && CardRules.isLegal(card, upCard, calledColor)) {
                return i;
            }
        }

        return -1;
    }

    static String chooseColor(ArrayList<String> hand) {
        int red = 0;
        int yellow = 0;
        int green = 0;
        int blue = 0;

        for (String card : hand) {
            String color = CardRules.color(card);

            if (color.equals("R")) {
                red++;
            } else if (color.equals("Y")) {
                yellow++;
            } else if (color.equals("G")) {
                green++;
            } else if (color.equals("B")) {
                blue++;
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
