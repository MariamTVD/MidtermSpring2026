/**
 * Central place for card-related rule logic.
 *
 * Cards are represented using the original string format:
 * R5, YS, BR, G+2, W, W4.
 *
 * Colors:
 * R = Red
 * Y = Yellow
 * G = Green
 * B = Blue
 *
 * Special cards:
 * S  = Skip
 * R  = Reverse
 * +2 = Draw Two
 * W  = Wild
 * W4 = Wild Draw Four
 */
public final class CardRules {

    private CardRules() {
    }

    static String color(String card) {
        if (card == null || card.length() == 0) {
            return "";
        }

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
        if (card == null || card.length() == 0) {
            return "UNKNOWN";
        }

        if (card.equals("W")) {
            return "WILD";
        }

        if (card.equals("W4")) {
            return "WILD_DRAW_FOUR";
        }

        if (card.endsWith("+2")) {
            return "DRAW_TWO";
        }

        if (card.endsWith("S")) {
            return "SKIP";
        }

        if (card.endsWith("R")) {
            return "REVERSE";
        }

        if (isNumberCard(card)) {
            return "NUMBER";
        }

        return "UNKNOWN";
    }

    static boolean isNumberCard(String card) {
        if (card == null || card.length() < 2) {
            return false;
        }

        String cardColor = color(card);

        if (cardColor.equals("")) {
            return false;
        }

        String numberPart = card.substring(1);

        try {
            int value = Integer.parseInt(numberPart);
            return value >= 0 && value <= 9;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    static int number(String card) {
        if (!rank(card).equals("NUMBER")) {
            return -1;
        }

        return Integer.parseInt(card.substring(1));
    }

    static boolean isWild(String card) {
        return rank(card).equals("WILD")
                || rank(card).equals("WILD_DRAW_FOUR");
    }

    static boolean isAction(String card) {
        String rank = rank(card);

        return rank.equals("SKIP")
                || rank.equals("REVERSE")
                || rank.equals("DRAW_TWO")
                || rank.equals("WILD")
                || rank.equals("WILD_DRAW_FOUR");
    }

    static boolean isValidColor(String color) {
        return color.equals("R")
                || color.equals("Y")
                || color.equals("G")
                || color.equals("B");
    }

    static boolean isValidCard(String card) {
        if (card == null) {
            return false;
        }

        String rank = rank(card);

        return rank.equals("NUMBER")
                || rank.equals("SKIP")
                || rank.equals("REVERSE")
                || rank.equals("DRAW_TWO")
                || rank.equals("WILD")
                || rank.equals("WILD_DRAW_FOUR");
    }

    static boolean isLegal(String candidate, String upCard, String calledColor) {
        if (!isValidCard(candidate) || !isValidCard(upCard)) {
            return false;
        }

        if (isWild(candidate)) {
            return true;
        }

        if (calledColor != null && !calledColor.equals("")) {
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

    static String colorName(String color) {
        if (color.equals("R")) {
            return "Red";
        }

        if (color.equals("Y")) {
            return "Yellow";
        }

        if (color.equals("G")) {
            return "Green";
        }

        if (color.equals("B")) {
            return "Blue";
        }

        return "Unknown";
    }
}
