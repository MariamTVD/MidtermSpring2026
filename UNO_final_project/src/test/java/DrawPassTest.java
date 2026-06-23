import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DrawPassTest {

    @BeforeEach
    void resetGameState() {
        Main.playerNames.clear();
        Main.humanPlayers.clear();
        Main.hands.clear();
        Main.deck.clear();
        Main.discard.clear();

        Main.playerNames.add("Bot1");
        Main.playerNames.add("Bot2");

        Main.humanPlayers.add(Boolean.FALSE);
        Main.humanPlayers.add(Boolean.FALSE);

        Main.hands.add(new ArrayList<String>());
        Main.hands.add(new ArrayList<String>());

        Main.currentPlayer = 0;
        Main.direction = 1;
        Main.quiet = true;
        Main.upCard = "R5";
        Main.calledColor = "";
    }

    @Test
    void botDrawsWhenNoLegalCardExists() {
        ArrayList<String> hand = Main.hands.get(0);
        hand.add("B3");

        Main.deck.add("G7");

        int chosenCardIndex = Main.handleDrawChoice("Bot1", hand);

        assertEquals(-1, chosenCardIndex);
        assertEquals(2, hand.size());
        assertTrue(hand.contains("G7"));
    }

    @Test
    void botCanPlayDrawnCardIfItIsLegal() {
        ArrayList<String> hand = Main.hands.get(0);
        hand.add("B3");

        Main.deck.add("R9");

        int chosenCardIndex = Main.handleDrawChoice("Bot1", hand);

        assertEquals(1, chosenCardIndex);
        assertEquals("R9", hand.get(chosenCardIndex));
    }

    @Test
    void drawUsesCalledColorAfterWild() {
        ArrayList<String> hand = Main.hands.get(0);
        hand.add("R3");

        Main.upCard = "W";
        Main.calledColor = "B";
        Main.deck.add("B9");

        int chosenCardIndex = Main.handleDrawChoice("Bot1", hand);

        assertEquals(1, chosenCardIndex);
        assertEquals("B9", hand.get(chosenCardIndex));
    }

    @Test
    void passingMovesToNextPlayerWhenNoCardIsPlayed() {
        Main.next();

        assertEquals(1, Main.currentPlayer);
    }
}
