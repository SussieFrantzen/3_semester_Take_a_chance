package app;

import app.config.HibernateConfig;
import app.config.ServerConfig;
import app.dao.CardDeckDAO;
import app.entities.CardDeck;
import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;

public class Main {

    public static void main(String[] args) {

        // Initialiser Hibernate
        EntityManagerFactory emf =
                HibernateConfig.getEntityManagerFactory();


        // Opret en kortbunke, hvis databasen er tom
        CardDeckDAO cardDeckDAO = new CardDeckDAO();

        if (cardDeckDAO.findAll().isEmpty()) {
            CardDeck newDeck = new CardDeck();

            CardDeck savedDeck = cardDeckDAO.create(newDeck);

            System.out.println(
                    "Kortbunke oprettet med ID: "
                            + savedDeck.getId()
            );
        }


        // Opret serveren
        ServerConfig serverConfig = new ServerConfig();
        Javalin app = serverConfig.createServer();

        // Start serveren
        app.start(7070);

        System.out.println(
                "Server startet på http://localhost:7070"
        );
    }
}
