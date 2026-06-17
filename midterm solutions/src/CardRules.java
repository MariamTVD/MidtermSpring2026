/** updated card rules: */

/**
 * Central place for card-related rule logic.
 *
 * Cards are represented using the original string format:
 * R5, YS, BR, G+2, W, W4.
 */
public final class CardRules {

    private CardRules() {
    }

    static String color(String card) {
        if (card.startsWith("R")) {
            return "R";
        }

        if (card.startsWith("Y")) {
            return "Y";
        }

        if (card.startsWith("G")) {
            return "G";
        }

        if (card.startsWith("B")) {
            return "B";
        }

        return "";
    }

    static String rank(String card) {
        if (card.equals("W")) {
            return "WILD";
        }

        if (card.equals("W4")) {
            return "WILD_DRAW_FOUR";
        }

        if (card.endsWith("S")) {
            return "SKIP";
        }

        if (card.endsWith("R")) {
            return "REVERSE";
        }

        if (card.endsWith("+2")) {
            return "DRAW_TWO";
        }

        return "NUMBER";
    }

    static int number(String card) {
        if (!rank(card).equals("NUMBER")) {
            return -1;
        }

        return Integer.parseInt(card.substring(1));
    }

    static boolean isLegal(String candidate, String upCard, String calledColor) {
        if (candidate.startsWith("W")) {
            return true;
        }

        if (!calledColor.equals("")) {
            return color(candidate).equals(calledColor);
        }

        if (color(candidate).equals(color(upCard))) {
            return true;
        }

        if (rank(candidate).equals(rank(upCard)) && !rank(candidate).equals("NUMBER")) {
            return true;
        }

        return rank(candidate).equals("NUMBER")
                && rank(upCard).equals("NUMBER")
                && number(candidate) == number(upCard);
    }

    static int points(String card) {
        String rank = rank(card);

        if (rank.equals("NUMBER")) {
            return number(card);
        }

        if (rank.equals("SKIP")
                || rank.equals("REVERSE")
                || rank.equals("DRAW_TWO")) {
            return 20;
        }

        if (rank.equals("WILD")
                || rank.equals("WILD_DRAW_FOUR")) {
            return 50;
        }

        return 0;
    }
}
