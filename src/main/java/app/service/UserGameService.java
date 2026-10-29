package app.service;
import app.entities.User;
import app.config.HibernateConfig;
import app.dao.UserDAO;
import app.dtos.CreateGameRequest;
import app.entities.GameBoard;
import app.exeption.ApiException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserGameService {

    private final EntityManagerFactory emf =
            HibernateConfig.getEntityManagerFactory();


    private final GameCreationService gameCreationService =
            new GameCreationService();


    private void validateUserExists(Integer userId) {
        try (EntityManager em = emf.createEntityManager()) {
            UserDAO userDAO = new UserDAO(em);

            if (userDAO.findById(userId) == null) {
                throw new ApiException(
                        404, "Brugeren blev ikke fundet.");
            }
        }
    }



    public app.entities.Game createGame(
            Integer userId,
            CreateGameRequest request
    ) throws java.io.IOException, InterruptedException {

        if (userId == null) {
            throw new ApiException(401, "Du skal være logget ind."
            );
        }

        if (request == null || request.gameBoardId() == null) {
            throw new ApiException(
                    400,
                    "Du skal vælge en spilleplade."
            );
        }
        validateUserExists(userId);


        try (EntityManager em = emf.createEntityManager()) {

            GameBoard board = em.find(
                    GameBoard.class,
                    request.gameBoardId()
            );

            if (board == null) {
                throw new ApiException(
                        404,
                        "Den valgte spilleplade findes ikke."
                );
            }

            if (board.getCardDeck() == null) {
                throw new ApiException(
                        409,
                        "Spillepladen mangler en kortbunke."
                );
            }

            if (board.getFields() == null
                    || board.getFields().isEmpty()) {
                throw new ApiException(
                        409,
                        "Spillepladen har ingen felter."
                );
            }
        }

        return gameCreationService.createGame(userId, request);
    }

    public List<Map<String, Object>> getAvailableGames(
            Integer userId
    ) {
        if (userId == null) {
            throw new ApiException(
                    401,
                    "Du skal være logget ind."
            );
        }

        validateUserExists(userId);

        try (EntityManager em = emf.createEntityManager()) {

            return em.createQuery(
                            "SELECT b FROM GameBoard b ORDER BY b.id",
                            GameBoard.class
                    )
                    .getResultList()
                    .stream()
                    .map(board -> {
                        Map<String, Object> result =
                                new LinkedHashMap<>();

                        result.put("id", board.getId());
                        result.put(
                                "boardName",
                                board.getBoardName()
                        );
                        result.put(
                                "subject",
                                board.getSubject()
                        );
                        result.put(
                                "boardLength",
                                board.getBoardLength()
                        );
                        result.put(
                                "gameName",
                                board.getGameName() == null
                                        ? null
                                        : board.getGameName().getName()
                        );

                        return result;
                    })
                    .collect(Collectors.toList());
        }
    }
}