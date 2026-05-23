import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles CLI interaction separately from game rules.
 */
public final class ConsolePrompter {
    private ConsolePrompter() {
    }

    static int requestCardChoice(Scanner scanner,
                                 ArrayList<String> hand,
                                 String topCard,
                                 String forcedColor) {

        while (true) {
            System.out.print("Enter card index/code or draw: ");

            String value = scanner.nextLine()
                    .trim()
                    .toUpperCase();

            if (value.equals("DRAW")) {
                return -1;
            }

            try {
                int parsed = Integer.parseInt(value);

                if (parsed >= 0 && parsed < hand.size()) {
                    return parsed;
                }
            } catch (Exception ignored) {
            }

            for (int i = 0; i < hand.size(); i++) {
                String card = hand.get(i);

                if (card.equals(value)) {
                    if (CardRules.isLegal(card, topCard, forcedColor)) {
                        return i;
                    }

                    System.out.println("Illegal move.");
                }
            }

            System.out.println("Input not recognized.");
        }
    }

    static String requestColor(Scanner scanner) {
        while (true) {
            System.out.print("Choose color R/Y/G/B: ");

            String color = scanner.nextLine()
                    .trim()
                    .toUpperCase();

            if (color.equals("R")
                    || color.equals("Y")
                    || color.equals("G")
                    || color.equals("B")) {
                return color;
            }

            System.out.println("Invalid color.");
        }
    }
}
