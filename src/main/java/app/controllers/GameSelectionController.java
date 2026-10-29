package app.controllers;

import app.dao.GameBoardDAO;
import app.dao.GameNameDAO;
import app.dtos.GameBoardDTO;
import app.dtos.GameNameDTO;
import app.entities.GameBoard;
import app.entities.GameName;
import io.javalin.http.Context;

import java.util.List;

public class GameSelectionController {

    private final GameNameDAO gameNameDAO = new GameNameDAO();
    private final GameBoardDAO gameBoardDAO = new GameBoardDAO();

    public void getGameNames(Context ctx) {
        List<GameNameDTO> games = gameNameDAO.findAllWithCardDeck()
                .stream()
                .map(game -> new GameNameDTO(
                        game.getId(),
                        game.getName()
                ))
                .toList();

        ctx.json(games);
    }

    public void getGameBoards(Context ctx) {
        Long gameNameId;

        try {
            gameNameId = Long.parseLong(
                    ctx.queryParam("gameNameId")
            );
        } catch (NumberFormatException | NullPointerException e) {
            ctx.status(400).json(
                    java.util.Map.of(
                            "error",
                            "Et gyldigt gameNameId er påkrævet."
                    )
            );
            return;
        }

        GameName gameName = gameNameDAO.findById(gameNameId)
                .orElse(null);

        if (gameName == null) {
            ctx.status(404).json(
                    java.util.Map.of("error", "Spillet blev ikke fundet.")
            );
            return;
        }

        List<GameBoard> boards =
                gameBoardDAO.findByGameNameId(gameNameId);

        List<GameBoardDTO> result = boards.stream()
                .map(board -> new GameBoardDTO(
                        board.getId(),
                        board.getBoardName(),
                        board.getSubject(),
                        board.getBoardLength().name(),
                        board.getGameName().getId(),
                        board.getGameName().getName()
                ))
                .toList();

        ctx.json(result);
    }
}