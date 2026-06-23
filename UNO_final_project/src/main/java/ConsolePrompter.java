import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles console input.
 *
 * This separates human input from the actual game rules.
 */
public final class ConsolePrompter {

    private ConsolePrompter() {
    }

    static int askHumanCardChoice(Scanner scanner,
                                  ArrayList<String> hand,
                                  String upCard,
                                  String calledColor) {
        while (true) {
            System.out.print("Choose card index/code, draw, hand, help, or quit: ");

            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("DRAW")) {
                return -1;
            }

            if (input.equals("HAND")) {
                printHand(hand);
                continue;
            }

            if (input.equals("HELP")) {
                printTurnHelp();
                continue;
            }

            if (input.equals("QUIT")) {
                return -99;
            }

            try {
                int index = Integer.parseInt(input);

                if (index >= 0 && index < hand.size()) {
                    if (CardRules.isLegal(hand.get(index), upCard, calledColor)) {
                        return index;
                    }

                    System.out.println("That card is not legal right now.");
                    continue;
                }

                System.out.println("That index is outside your hand.");
                continue;
            } catch (NumberFormatException ignored) {
            }

            for (int i = 0; i < hand.size(); i++) {
                if (hand.get(i).equals(input)) {
                    if (CardRules.isLegal(hand.get(i), upCard, calledColor)) {
                        return i;
                    }

                    System.out.println("That card is not legal right now.");
                    break;
                }
            }

            System.out.println("Card not found. Type help to see commands.");
        }
    }

    static boolean askPlayDrawnCard(Scanner scanner, String drawnCard) {
        while (true) {
            System.out.print("Play drawn card " + drawnCard + "? y/n: ");

            String answer = scanner.nextLine().trim().toLowerCase();

            if (answer.equals("y") || answer.equals("yes")) {
                return true;
            }

            if (answer.equals("n") || answer.equals("no")) {
                return false;
            }

            System.out.println("Please answer y or n.");
        }
    }

    static String askColor(Scanner scanner) {
        while (true) {
            System.out.print("Call color R/Y/G/B: ");

            String input = scanner.nextLine().trim().toUpperCase();

            if (CardRules.isValidColor(input)) {
                return input;
            }

            System.out.println("Bad color. Choose R, Y, G, or B.");
        }
    }

    static boolean askUnoCall(Scanner scanner, String playerName) {
        while (true) {
            System.out.print(playerName + ", you have one card left. Type UNO to call UNO, or press Enter to skip: ");

            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("UNO")) {
                return true;
            }

            if (input.equals("")) {
                return false;
            }

            System.out.println("Type UNO or press Enter.");
        }
    }

    static void waitForHumanTurn(Scanner scanner, String playerName) {
        System.out.println("Pass the computer to " + playerName + ".");
        System.out.print("Press Enter when " + playerName + " is ready: ");
        scanner.nextLine();
    }

    static void printHand(ArrayList<String> hand) {
        System.out.println("Your hand:");

        for (int i = 0; i < hand.size(); i++) {
            System.out.println(i + ": " + hand.get(i));
        }
    }

    static void printTurnHelp() {
        System.out.println("Commands:");
        System.out.println("- Type a card index, for example: 0");
        System.out.println("- Type a card code, for example: R5, BS, G+2, W, W4");
        System.out.println("- Type draw to draw a card");
        System.out.println("- Type hand to show your hand again");
        System.out.println("- Type help to show this help");
        System.out.println("- Type quit to stop the game");
    }
}
