import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActionCardTest {

    @BeforeEach
    void resetGameState() {
        Main.playerNames.clear();
        Main.humanPlayers.clear();
        Main.hands.clear();
        Main.deck.clear();
        Main.discard.clear();

        Main.playerNames.add("Bot1");
        Main.playerNames.add("Bot2");
        Main.playerNames.add("Bot3");

        Main.humanPlayers.add(Boolean.FALSE);
        Main.humanPlayers.add(Boolean.FALSE);
        Main.humanPlayers.add(Boolean.FALSE);

        Main.hands.add(new ArrayList<String>());
        Main.hands.add(new ArrayList<String>());
        Main.hands.add(new ArrayList<String>());

        Main.currentPlayer = 0;
        Main.direction = 1;
        Main.quiet = true;

        for (int i = 0; i < 20; i++) {
            Main.deck.add("R" + (i % 10));
        }
    }

    @Test
    void skipSkipsNextPlayer() {
        Main.applyCardEffect("RS");

        assertEquals(2, Main.currentPlayer);
    }

    @Test
    void reverseChangesDirectionInThreePlayerGame() {
        Main.applyCardEffect("RR");

        assertEquals(-1, Main.direction);
        assertEquals(2, Main.currentPlayer);
    }

    @Test
    void reverseActsLikeSkipInTwoPlayerGame() {
        Main.playerNames.remove(2);
        Main.humanPlayers.remove(2);
        Main.hands.remove(2);

        Main.applyCardEffect("RR");

        assertEquals(-1, Main.direction);
        assertEquals(0, Main.currentPlayer);
    }

    @Test
    void drawTwoMakesNextPlayerDrawTwoAndLoseTurn() {
        Main.applyCardEffect("R+2");

        assertEquals(2, Main.hands.get(1).size());
        assertEquals(2, Main.currentPlayer);
    }

    @Test
    void wildDrawFourMakesNextPlayerDrawFourAndLoseTurn() {
        Main.applyCardEffect("W4");

        assertEquals(4, Main.hands.get(1).size());
        assertEquals(2, Main.currentPlayer);
    }

    @Test
    void normalNumberCardMovesToNextPlayer() {
        Main.applyCardEffect("R5");

        assertEquals(1, Main.currentPlayer);
    }
}
