package app.dao;
import app.entities.GameBoard;

public class GameBoardDAO extends JPADAO<GameBoard, Long> {

    public GameBoardDAO() {
        super(GameBoard.class);
    }
}