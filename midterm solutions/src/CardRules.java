/**
 * Stores utility logic related to the current card representation.
 *
 * Supported examples:
 * R7, BS, Y+2, W, W4.
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

    static boolean isLegal(String candidate,
                           String topCard,
                           String forcedColor) {

        if (candidate.startsWith("W")) {
            return true;
        }

        if (color(candidate).equals(color(topCard))) {
            return true;
        }

        if (!forcedColor.equals("")
                && color(candidate).equals(forcedColor)) {
            return true;
        }

        if (rank(candidate).equals(rank(topCard))
                && !rank(candidate).equals("NUMBER")) {
            return true;
        }

        return rank(candidate).equals("NUMBER")
                && rank(topCard).equals("NUMBER")
                && number(candidate) == number(topCard);
    }

    static int points(String card) {
        String type = rank(card);

        if (type.equals("NUMBER")) {
            return number(card);
        }

        if (type.equals("SKIP")
                || type.equals("REVERSE")
                || type.equals("DRAW_TWO")) {
            return 20;
        }

        if (type.equals("WILD")
                || type.equals("WILD_DRAW_FOUR")) {
            return 50;
        }

        return 0;
    }
}
