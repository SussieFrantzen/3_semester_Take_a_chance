
package app.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoinTest {

    @Test
    void flipReturnsHeadsOrTails() {
        Coin coin = new Coin();

        String result = coin.flip();

        assertTrue(
                result.equals("Heads") || result.equals("Tails")
        );
    }

    @Test
    void flipUpdatesHeadsField() {
        Coin coin = new Coin();

        String result = coin.flip();

        assertEquals("Heads".equals(result), coin.isHeads());
    }

    @Test
    void flipCanBeCalledMultipleTimes() {
        Coin coin = new Coin();

        for (int i = 0; i < 100; i++) {
            String result = coin.flip();

            assertTrue(
                    result.equals("Heads") || result.equals("Tails")
            );

            assertEquals(
                    "Heads".equals(result),
                    coin.isHeads()
            );
        }
    }
}
