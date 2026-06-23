import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnoCallPenaltyTest {

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

        for (int i = 0; i < 20; i++) {
            Main.deck.add("B" + (i % 10));
        }
    }

    @Test
    void botWithOneCardLeftDoesNotReceiveUnoPenalty() {
        ArrayList<String> hand = Main.hands.get(0);

        hand.add("R7");

        Main.handleUnoCallIfNeeded("Bot1", hand);

        assertEquals(1, hand.size());
    }

    @Test
    void noUnoCheckHappensWhenPlayerHasMoreThanOneCard() {
        ArrayList<String> hand = Main.hands.get(0);

        hand.add("R7");
        hand.add("B3");

        Main.handleUnoCallIfNeeded("Bot1", hand);

        assertEquals(2, hand.size());
    }

    @Test
    void playingCardToOneCardStateTriggersUnoHandlingForBot() {
        ArrayList<String> hand = Main.hands.get(0);

        hand.add("R7");
        hand.add("R8");

        Main.playLegalCard("Bot1", hand, 0, "R7");

        assertEquals(1, hand.size());
    }

    @Test
    void missedUnoPenaltyAddsTwoCardsWhenAppliedManually() {
        ArrayList<String> hand = Main.hands.get(0);

        hand.add("R7");

        hand.add(Main.draw());
        hand.add(Main.draw());

        assertEquals(3, hand.size());
    }
}
