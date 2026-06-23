import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CardRulesTest {

    @Test
    void extractsCardColor() {
        assertEquals("R", CardRules.color("R5"));
        assertEquals("Y", CardRules.color("YS"));
        assertEquals("G", CardRules.color("G+2"));
        assertEquals("B", CardRules.color("BR"));
        assertEquals("", CardRules.color("W"));
        assertEquals("", CardRules.color("W4"));
    }

    @Test
    void extractsCardRank() {
        assertEquals("NUMBER", CardRules.rank("R5"));
        assertEquals("SKIP", CardRules.rank("YS"));
        assertEquals("REVERSE", CardRules.rank("BR"));
        assertEquals("DRAW_TWO", CardRules.rank("G+2"));
        assertEquals("WILD", CardRules.rank("W"));
        assertEquals("WILD_DRAW_FOUR", CardRules.rank("W4"));
    }

    @Test
    void extractsCardNumber() {
        assertEquals(0, CardRules.number("R0"));
        assertEquals(5, CardRules.number("B5"));
        assertEquals(9, CardRules.number("G9"));
        assertEquals(-1, CardRules.number("RS"));
        assertEquals(-1, CardRules.number("W"));
    }

    @Test
    void allowsSameColorPlay() {
        assertTrue(CardRules.isLegal("R2", "R9", ""));
    }

    @Test
    void allowsSameNumberPlay() {
        assertTrue(CardRules.isLegal("G9", "R9", ""));
    }

    @Test
    void allowsSameActionTypePlay() {
        assertTrue(CardRules.isLegal("BS", "RS", ""));
        assertTrue(CardRules.isLegal("Y+2", "G+2", ""));
        assertTrue(CardRules.isLegal("RR", "BR", ""));
    }

    @Test
    void allowsWildCards() {
        assertTrue(CardRules.isLegal("W", "R9", ""));
        assertTrue(CardRules.isLegal("W4", "B3", ""));
    }

    @Test
    void rejectsIllegalMismatch() {
        assertFalse(CardRules.isLegal("B3", "R9", ""));
        assertFalse(CardRules.isLegal("G+2", "R9", ""));
        assertFalse(CardRules.isLegal("YS", "B7", ""));
    }

    @Test
    void usesCalledColorAfterWild() {
        assertTrue(CardRules.isLegal("B3", "W", "B"));
        assertTrue(CardRules.isLegal("B+2", "W4", "B"));

        assertFalse(CardRules.isLegal("R3", "W", "B"));
        assertFalse(CardRules.isLegal("G+2", "W4", "B"));
    }

    @Test
    void calculatesCardPoints() {
        assertEquals(5, CardRules.points("R5"));
        assertEquals(0, CardRules.points("B0"));

        assertEquals(20, CardRules.points("RS"));
        assertEquals(20, CardRules.points("YR"));
        assertEquals(20, CardRules.points("G+2"));

        assertEquals(50, CardRules.points("W"));
        assertEquals(50, CardRules.points("W4"));
    }

    @Test
    void validatesCardFormat() {
        assertTrue(CardRules.isValidCard("R0"));
        assertTrue(CardRules.isValidCard("B9"));
        assertTrue(CardRules.isValidCard("GS"));
        assertTrue(CardRules.isValidCard("YR"));
        assertTrue(CardRules.isValidCard("R+2"));
        assertTrue(CardRules.isValidCard("W"));
        assertTrue(CardRules.isValidCard("W4"));

        assertFalse(CardRules.isValidCard(""));
        assertFalse(CardRules.isValidCard("X5"));
        assertFalse(CardRules.isValidCard("R10"));
        assertFalse(CardRules.isValidCard("BAD"));
    }
}
