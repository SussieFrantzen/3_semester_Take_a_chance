package app.config;

import app.entities.Player;
import org.hibernate.cfg.Configuration;

final class EntityRegistry {

    private EntityRegistry() {}

    static void registerEntities(Configuration configuration) {
        configuration.addAnnotatedClass(Player.class);
        // TODO: Add more entities here...
    }
}