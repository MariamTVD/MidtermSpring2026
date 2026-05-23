import java.util.ArrayList;
import java.util.Scanner;

/**
 * Simplified alternate Main version.
 * Preserves the same helper structure and compatibility style.
 */
public class Main {

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("UNO Refactored Alternate Version");
        System.out.println("Project structure loaded successfully.");
    }

    static boolean isLegal(String card, String topCard, String calledColor) {
        return CardRules.isLegal(card, topCard, calledColor);
    }

    static int chooseBotCard(ArrayList<String> hand,
                             String topCard,
                             String calledColor) {
        return BotStrategy.selectPlayableCard(hand, topCard, calledColor);
    }

    static String chooseBotColor(ArrayList<String> hand) {
        return BotStrategy.selectColor(hand);
    }

    static int askHuman(ArrayList<String> hand,
                        String topCard,
                        String calledColor) {
        return ConsolePrompter.requestCardChoice(
                scanner,
                hand,
                topCard,
                calledColor
        );
    }

    static String askColor() {
        return ConsolePrompter.requestColor(scanner);
    }
}
