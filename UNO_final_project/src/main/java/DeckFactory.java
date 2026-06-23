import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Creates UNO decks.
 *
 * This class keeps deck composition separate from Main so it can be tested.
 *
 * Card format:
 * R0, R1 ... R9
 * RS = Red Skip
 * RR = Red Reverse
 * R+2 = Red Draw Two
 * W = Wild
 * W4 = Wild Draw Four
 */
public final class DeckFactory {

    private DeckFactory() {
    }

    static ArrayList<String> createDeck() {
        ArrayList<String> deck = new ArrayList<String>();

        String[] colors = {"R", "Y", "G", "B"};

        for (String color : colors) {
            addNumberCards(deck, color);
            addActionCards(deck, color);
        }

        addWildCards(deck);

        return deck;
    }

    static ArrayList<String> createShuffledDeck(Random random) {
        ArrayList<String> deck = createDeck();
        Collections.shuffle(deck, random);
        return deck;
    }

    private static void addNumberCards(ArrayList<String> deck, String color) {
        deck.add(color + "0");

        for (int number = 1; number <= 9; number++) {
            deck.add(color + number);
            deck.add(color + number);
        }
    }

    private static void addActionCards(ArrayList<String> deck, String color) {
        deck.add(color + "S");
        deck.add(color + "S");

        deck.add(color + "R");
        deck.add(color + "R");

        deck.add(color + "+2");
        deck.add(color + "+2");
    }

    private static void addWildCards(ArrayList<String> deck) {
        for (int i = 0; i < 4; i++) {
            deck.add("W");
            deck.add("W4");
        }
    }

    static int countCard(ArrayList<String> deck, String card) {
        int count = 0;

        for (String deckCard : deck) {
            if (deckCard.equals(card)) {
                count++;
            }
        }

        return count;
    }

    static int countRank(ArrayList<String> deck, String rank) {
        int count = 0;

        for (String card : deck) {
            if (CardRules.rank(card).equals(rank)) {
                count++;
            }
        }

        return count;
    }

    static int countColor(ArrayList<String> deck, String color) {
        int count = 0;

        for (String card : deck) {
            if (CardRules.color(card).equals(color)) {
                count++;
            }
        }

        return count;
    }
}

