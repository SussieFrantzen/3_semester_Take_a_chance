package app.config;

import app.entities.*;
import org.hibernate.cfg.Configuration;

final class EntityRegistry {

    private EntityRegistry() {}

    static void registerEntities(Configuration configuration) {
        configuration.addAnnotatedClass(Player.class);
        // TODO: Add more entities here...
        configuration.addAnnotatedClass(CardDeck.class);
        configuration.addAnnotatedClass(FateCard.class);
        configuration.addAnnotatedClass(GameBoard.class);
        configuration.addAnnotatedClass(QuestionCard.class);
        configuration.addAnnotatedClass(User.class);
    }
}