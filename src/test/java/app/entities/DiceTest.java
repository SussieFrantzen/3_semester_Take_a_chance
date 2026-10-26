
package app.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiceTest {

    @Test
    void rollReturnsValueBetweenOneAndSix() {
        Dice dice = new Dice();

        int result = dice.roll();

        assertTrue(
                result >= 1 && result <= 6,
                "Terningen skal give mellem 1 og 6 øjne."
        );
    }

    @Test
    void rollUpdatesStoredValue() {
        Dice dice = new Dice();

        int result = dice.roll();

        assertEquals(result, dice.getValue());
    }

    @Test
    void repeatedRollsAlwaysReturnValidValues() {
        Dice dice = new Dice();

        for (int i = 0; i < 100; i++) {
            int result = dice.roll();

            assertTrue(
                    result >= 1 && result <= 6,
                    "Ugyldigt terningresultat: " + result
            );

            assertEquals(result, dice.getValue());
        }
    }
}
