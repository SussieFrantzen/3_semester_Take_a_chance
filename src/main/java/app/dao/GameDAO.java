package app.dao;

import app.entities.Game;

public class GameDAO extends JPADAO<Game, Long> {

    public GameDAO() {
        super(Game.class);
    }
}