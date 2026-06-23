import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeckFactoryTest {

    @Test
    void createsClassicUnoDeckWith108Cards() {
        ArrayList<String> deck = DeckFactory.createDeck();

        assertEquals(108, deck.size());
    }

    @Test
    void createsFourColorsWithCorrectColorCardCounts() {
        ArrayList<String> deck = DeckFactory.createDeck();

        assertEquals(25, DeckFactory.countColor(deck, "R"));
        assertEquals(25, DeckFactory.countColor(deck, "Y"));
        assertEquals(25, DeckFactory.countColor(deck, "G"));
        assertEquals(25, DeckFactory.countColor(deck, "B"));
    }

    @Test
    void createsCorrectNumberCards() {
        ArrayList<String> deck = DeckFactory.createDeck();

        String[] colors = {"R", "Y", "G", "B"};

        for (String color : colors) {
            assertEquals(1, DeckFactory.countCard(deck, color + "0"));

            for (int number = 1; number <= 9; number++) {
                assertEquals(2, DeckFactory.countCard(deck, color + number));
            }
        }
    }

    @Test
    void createsCorrectActionCards() {
        ArrayList<String> deck = DeckFactory.createDeck();

        String[] colors = {"R", "Y", "G", "B"};

        for (String color : colors) {
            assertEquals(2, DeckFactory.countCard(deck, color + "S"));
            assertEquals(2, DeckFactory.countCard(deck, color + "R"));
            assertEquals(2, DeckFactory.countCard(deck, color + "+2"));
        }

        assertEquals(8, DeckFactory.countRank(deck, "SKIP"));
        assertEquals(8, DeckFactory.countRank(deck, "REVERSE"));
        assertEquals(8, DeckFactory.countRank(deck, "DRAW_TWO"));
    }

    @Test
    void createsCorrectWildCards() {
        ArrayList<String> deck = DeckFactory.createDeck();

        assertEquals(4, DeckFactory.countCard(deck, "W"));
        assertEquals(4, DeckFactory.countCard(deck, "W4"));

        assertEquals(4, DeckFactory.countRank(deck, "WILD"));
        assertEquals(4, DeckFactory.countRank(deck, "WILD_DRAW_FOUR"));
    }
}

