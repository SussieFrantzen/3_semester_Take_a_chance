package app.dao;

import app.entities.GameName;

public class GameNameDAO extends JPADAO<GameName, Long> {

    public GameNameDAO() {
        super(GameName.class);
    }
}