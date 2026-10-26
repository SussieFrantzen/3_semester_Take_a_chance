package app.dao;

import app.entities.CardDeck;

public class CardDeckDAO extends JPADAO<CardDeck, Long> {

    public CardDeckDAO() {
        super(CardDeck.class);
    }
}