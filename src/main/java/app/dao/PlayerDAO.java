package app.dao;

import app.entities.Player;

public class PlayerDAO extends JPADAO<Player, Long> {

    public PlayerDAO() {
        super(Player.class);
    }
}