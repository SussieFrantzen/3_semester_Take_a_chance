/*package app;

import app.config.HibernateConfig;
import app.controllers.QuestionCardController;
import app.dao.CardDeckDAO;
import app.dao.QuestionCardDAO;
import app.entities.CardDeck;
import app.route.Routes;
import app.service.OpenAiQuestionCardService;

import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;

public class Main {

    public static void main(String[] args) {

        // Hibernate
        EntityManagerFactory emf =
                HibernateConfig.getEntityManagerFactory();

        // DAO'er
        QuestionCardDAO questionCardDAO =
                new QuestionCardDAO();

        CardDeckDAO cardDeckDAO =
                new CardDeckDAO();

        // Opret kun en kortbunke, hvis databasen ikke allerede har en
        if (cardDeckDAO.findAll().isEmpty()) {
            CardDeck newDeck = new CardDeck();

            CardDeck savedDeck =
                    cardDeckDAO.save(newDeck);

            System.out.println(
                    "Kortbunke oprettet med ID: "
                            + savedDeck.getId()
            );
        }

        // OpenAI-service
        OpenAiQuestionCardService service =
                new OpenAiQuestionCardService(questionCardDAO);

        // Opret Routes
        Routes routes = new Routes();

        // Opret Javalin-server
        Javalin app = Javalin.create(config -> {
            config.router.apiBuilder(routes.getRoutes());
        });

        // Behold din originale QuestionCardController
        QuestionCardController questionCardController =
                new QuestionCardController(
                        questionCardDAO,
                        cardDeckDAO,
                        service
                );

        questionCardController.registerRoutes(app);

        // Start server
        app.start(7070);

        System.out.println(
                "Server startet på http://localhost:7070"
        );
    }
}


 */
 /*
package app;

import app.config.HibernateConfig;
import app.controllers.QuestionCardController;
import app.dao.CardDeckDAO;
import app.dao.QuestionCardDAO;
import app.entities.CardDeck;
import app.route.QuestionCardRoute;
import app.route.Routes;
import app.service.OpenAiQuestionCardService;

import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;

public class Main {

    public static void main(String[] args) {

        // Initialiser Hibernate
        EntityManagerFactory emf =
                HibernateConfig.getEntityManagerFactory();

        // Opret DAO'er
        QuestionCardDAO questionCardDAO = new QuestionCardDAO();
        CardDeckDAO cardDeckDAO = new CardDeckDAO();

        OpenAiQuestionCardService service = new OpenAiQuestionCardService(questionCardDAO);

        QuestionCardController questionCardController =
                new QuestionCardController(
                        questionCardDAO,
                        cardDeckDAO,
                        service
                );

        // Opret en kortbunke, hvis databasen er tom
        if (cardDeckDAO.findAll().isEmpty()) {

            CardDeck newDeck = new CardDeck();

            CardDeck savedDeck =
                    cardDeckDAO.save(newDeck);

            System.out.println(
                    "Kortbunke oprettet med ID: "
                            + savedDeck.getId()
            );
        }

        Javalin app = Javalin.create();

        questionCardController.registerRoutes(app);


        // Opret routes til spørgsmålskort
        QuestionCardRoute questionCardRoute =
                new QuestionCardRoute(
                        questionCardController
                );

        // Saml routes
        Routes routes = new Routes(questionCardRoute);

        // Opret Javalin og registrer routes
        Javalin app = Javalin.create(config -> {
            config.router.apiBuilder(routes.getRoutes());
        });

        // Start server
        app.start(7070);

        System.out.println(
                "Server startet på http://localhost:7070"
        );
    }
}
*/
/*
package app;

import app.config.HibernateConfig;
import app.controllers.QuestionCardController;
import app.dao.CardDeckDAO;
import app.dao.QuestionCardDAO;
import app.entities.CardDeck;
import app.route.Routes;
import app.service.OpenAiQuestionCardService;

import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;

public class Main {

    public static void main(String[] args) {

        // Initialiser Hibernate
        EntityManagerFactory emf =
                HibernateConfig.getEntityManagerFactory();

        // Opret DAO'er
        QuestionCardDAO questionCardDAO =
                new QuestionCardDAO();

        CardDeckDAO cardDeckDAO =
                new CardDeckDAO();

        // Opret en kortbunke, hvis databasen er tom
        if (cardDeckDAO.findAll().isEmpty()) {

            CardDeck newDeck = new CardDeck();

            CardDeck savedDeck =
                    cardDeckDAO.save(newDeck);

            System.out.println(
                    "Kortbunke oprettet med ID: "
                            + savedDeck.getId()
            );
        }

        // Opret OpenAI-service
        OpenAiQuestionCardService service =
                new OpenAiQuestionCardService(
                        questionCardDAO
                );

        // Opret controller
        QuestionCardController questionCardController =
                new QuestionCardController(
                        questionCardDAO,
                        cardDeckDAO,
                        service
                );



        // Registrer spil og brugere via Routes
        Routes routes = new Routes();
        // Opret Javalin
        Javalin app = Javalin.create(config -> {
            config.router.apiBuilder(routes.getRoutes());
        });
        // Registrer spørgsmålskort
        questionCardController.registerRoutes(app);


        // Start serveren
        app.start(7070);

        System.out.println(
                "Server startet på http://localhost:7070"
        );
    }
}
*/

package app;

import app.config.HibernateConfig;
import app.controllers.QuestionCardController;
import app.dao.CardDeckDAO;
import app.dao.QuestionCardDAO;
import app.entities.CardDeck;
import app.route.Routes;
import app.service.OpenAiQuestionCardService;

import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;

public class Main {

    public static void main(String[] args) {

        // Initialiser Hibernate
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        // Opret DAO'er
        QuestionCardDAO questionCardDAO = new QuestionCardDAO();

        CardDeckDAO cardDeckDAO = new CardDeckDAO();

        // Opret en kortbunke, hvis databasen er tom
        if (cardDeckDAO.findAll().isEmpty()) {
            CardDeck newDeck = new CardDeck();

            CardDeck savedDeck = cardDeckDAO.save(newDeck);

            System.out.println("Kortbunke oprettet med ID: " + savedDeck.getId()
            );
        }

        // Opret OpenAI-service
        OpenAiQuestionCardService service = new OpenAiQuestionCardService(questionCardDAO);

        // Opret controller
        QuestionCardController questionCardController =
                new QuestionCardController(questionCardDAO, cardDeckDAO, service);

        // Opret routes til spil og brugere
        Routes routes = new Routes();

        // Opret Javalin-server
        Javalin app = Javalin.create(config -> {
            config.router.apiBuilder(
                    routes.getRoutes()
            );
        });

        // Registrer spørgsmålskort
        questionCardController.registerRoutes(app);

        // Start serveren
        app.start(7070);

        System.out.println(
                "Server startet på http://localhost:7070"
        );
    }
}

