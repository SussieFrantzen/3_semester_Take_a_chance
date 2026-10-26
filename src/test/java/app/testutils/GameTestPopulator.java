package app.testutils;

import app.entities.CardDeck;
import app.entities.Field;
import app.entities.Game;
import app.entities.GameBoard;
import app.entities.GameName;
import app.entities.Player;
import app.entities.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public final class GameTestPopulator {

    private GameTestPopulator() {
    }

    public static Map<String, Object> populate(
            EntityManagerFactory emf) {

        try (EntityManager em = emf.createEntityManager()) {

            em.getTransaction().begin();

            try {
                // Opret bruger
                User user = new User(
                        "testuser",
                        "testpassword",
                        "testuser@example.com"
                );

                em.persist(user);

                // Opret kortbunke
                CardDeck deck = new CardDeck();

                em.persist(deck);

                // Opret spilnavn
                GameName gameName = new GameName();

                gameName.setName("Testspil");
                gameName.setCardDeck(deck);

                em.persist(gameName);

                // Opret spilleplade
                GameBoard board = new GameBoard();

                board.setBoardName("Test spilleplade");
                board.setSubject("Matematik");
                board.setGameName(gameName);
                board.setCardDeck(deck);

                em.persist(board);

                // Opret felter på spillepladen
                Field field1 = new Field("START", 0, 0);
                Field field2 = new Field("QUESTIONFIELD", 1, 0);
                Field field3 = new Field("FATEFIELD", 2, 0);
                Field field4 = new Field("QUESTIONFIELD", 3, 0);
                Field field5 = new Field("NORMAL", 4, 0);
                Field field6 = new Field("QUESTIONFIELD", 5, 0);
                Field field7 = new Field("FATEFIELD", 6, 0);
                Field field8 = new Field("QUESTIONFIELD", 7, 0);
                Field field9 = new Field("NORMAL", 8, 0);
                Field field10 = new Field("FINISH", 9, 0);

                Field[] fields = {
                        field1, field2, field3, field4, field5,
                        field6, field7, field8, field9, field10
                };

                for (Field field : fields) {
                    field.setGameBoard(board);
                    board.getFields().add(field);
                }

                // Opret spil
                Game game = new Game();

                game.setUser(user);
                game.setGameBoard(board);

                em.persist(game);

                // Opret spillere og forbind dem til spillet
                Player player1 = new Player(
                        "Testspiller 1",
                        8,
                        user
                );

                Player player2 = new Player(
                        "Testspiller 2",
                        12,
                        user
                );

                game.addPlayer(player1);
                game.addPlayer(player2);

                em.flush();

                // Returner testdata, så testene kan genbruge dem
                Map<String, Object> seeded = new LinkedHashMap<>();

                seeded.put("user", user);
                seeded.put("deck", deck);
                seeded.put("gameName", gameName);
                seeded.put("board", board);
                seeded.put("game", game);
                seeded.put("player1", player1);
                seeded.put("player2", player2);

                em.getTransaction().commit();

                return seeded;

            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw e;
            }
        }
    }
}