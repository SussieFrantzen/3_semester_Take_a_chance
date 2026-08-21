package app.config;

import app.entities.GamePlayer;
import org.hibernate.cfg.Configuration;

final class EntityRegistry {

    private EntityRegistry() {}

    static void registerEntities(Configuration configuration) {
        configuration.addAnnotatedClass(GamePlayer.class);
        // TODO: Add more entities here...
    }
}