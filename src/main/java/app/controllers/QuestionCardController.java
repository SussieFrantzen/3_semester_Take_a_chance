
package app.controllers;

import app.dao.CardDeckDAO;
import app.dao.QuestionCardDAO;
import app.dtos.GenerateQuestionCardsRequest;
import app.dtos.QuestionCardDTO;
import app.entities.CardDeck;
import app.entities.QuestionCard;
import app.service.OpenAiQuestionCardService;

import io.javalin.Javalin;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class QuestionCardController {

    private final QuestionCardDAO questionCardDAO;
    private final CardDeckDAO cardDeckDAO;
    private final OpenAiQuestionCardService service;

    public QuestionCardController(
            QuestionCardDAO questionCardDAO,
            CardDeckDAO cardDeckDAO,
            OpenAiQuestionCardService service) {

        this.questionCardDAO = questionCardDAO;
        this.cardDeckDAO = cardDeckDAO;
        this.service = service;
    }

    public void registerRoutes(Javalin app) {

        // Generer spørgsmålskort
        app.post("/api/question-cards/generate", ctx -> {

            GenerateQuestionCardsRequest request;

            try {
                request = ctx.bodyAsClass(
                        GenerateQuestionCardsRequest.class
                );
            } catch (Exception e) {
                ctx.status(400).json(
                        Map.of("error", "Ugyldig JSON i forespørgslen.")
                );
                return;
            }

            if (request == null
                    || request.subject() == null
                    || request.subject().isBlank()
                    || request.ageGroup() == null
                    || request.ageGroup().isBlank()
                    || request.cardDeckId() == null) {

                ctx.status(400).json(
                        Map.of(
                                "error",
                                "Emne, aldersgruppe og kortbunke skal angives."
                        )
                );
                return;
            }

            CardDeck deck = cardDeckDAO
                    .findById(request.cardDeckId())
                    .orElse(null);

            if (deck == null) {
                ctx.status(404).json(
                        Map.of("error", "Kortbunken blev ikke fundet.")
                );
                return;
            }

            try {
                List<QuestionCard> cards =
                        service.generateAndSaveCards(
                                request.subject(),
                                request.ageGroup(),
                                deck,
                                10
                        );

                List<QuestionCardDTO> response = cards.stream()
                        .map(card -> new QuestionCardDTO(
                                card.getQuestion(),
                                card.getAnswer(),
                                card.getDifficulty()
                        ))
                        .toList();

                ctx.status(200).json(response);

            } catch (IllegalArgumentException e) {
                ctx.status(400).json(
                        Map.of("error", e.getMessage())
                );

            } catch (IOException e) {
                ctx.status(502).json(
                        Map.of(
                                "error",
                                "Kunne ikke generere spørgsmål via OpenAI."
                        )
                );

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                ctx.status(500).json(
                        Map.of("error", "Genereringen blev afbrudt.")
                );
            }
        });

        // Hent alle spørgsmålskort
        app.get("/api/question-cards", ctx -> {

            List<QuestionCardDTO> response =
                    questionCardDAO.findAll().stream()
                            .map(card -> new QuestionCardDTO(
                                    card.getQuestion(),
                                    card.getAnswer(),
                                    card.getDifficulty()
                            ))
                            .toList();

            ctx.json(response);
        });

        // Hent ét spørgsmålskort
        app.get("/api/question-cards/{id}", ctx -> {

            long id;

            try {
                id = Long.parseLong(ctx.pathParam("id"));

                if (id <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                ctx.status(400).json(
                        Map.of(
                                "error",
                                "ID skal være et positivt heltal."
                        )
                );
                return;
            }

            QuestionCard card = questionCardDAO
                    .findById(id)
                    .orElse(null);

            if (card == null) {
                ctx.status(404).json(
                        Map.of(
                                "error",
                                "Spørgsmålskortet blev ikke fundet."
                        )
                );
                return;
            }

            ctx.json(new QuestionCardDTO(
                    card.getQuestion(),
                    card.getAnswer(),
                    card.getDifficulty()
            ));
        });
    }
}

/*
    public void registerRoutes(Javalin app) {

        // Generer og gem spørgsmålskort
        app.post("/api/question-cards/generate", ctx -> {

            GenerateQuestionCardsRequest request =
                    ctx.bodyAsClass(
                            GenerateQuestionCardsRequest.class
                    );

            if (request.subject() == null
                    || request.subject().isBlank()
                    || request.ageGroup() == null
                    || request.ageGroup().isBlank()
                    || request.cardDeckId() == null) {

                ctx.status(400).json(
                        java.util.Map.of(
                                "error",
                                "Emne, aldersgruppe og kortbunke skal angives."
                        )
                );
                return;
            }

            CardDeck deck = cardDeckDAO
                    .findById(request.cardDeckId())
                    .orElse(null);

            if (deck == null) {
                ctx.status(404).json(
                        java.util.Map.of(
                                "error",
                                "Kortbunken blev ikke fundet."
                        )
                );
                return;
            }

            try {
                int amountToGenerate = 10;

                List<QuestionCard> cards =
                        service.generateAndSaveCards(
                                request.subject(),
                                request.ageGroup(),
                                deck,
                                amountToGenerate
                        );

                List<QuestionCardDTO> response = cards.stream()
                        .map(card -> new QuestionCardDTO(
                                card.getQuestion(),
                                card.getAnswer(),
                                card.getDifficulty()
                        ))
                        .toList();

                ctx.status(200).json(response);

            } catch (IllegalArgumentException e) {
                ctx.status(400).json(
                        java.util.Map.of("error", e.getMessage())
                );

            } catch (IOException e) {
                ctx.status(502).json(
                        java.util.Map.of(
                                "error",
                                "Kunne ikke generere spørgsmål via OpenAI."
                        )
                );

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                ctx.status(500).json(
                        java.util.Map.of(
                                "error",
                                "Genereringen blev afbrudt."
                        )
                );
            }
        });

        // Hent alle spørgsmålskort
        app.get("/api/question-cards", ctx -> {

            List<QuestionCardDTO> response =
                    questionCardDAO.findAll().stream()
                            .map(card -> new QuestionCardDTO(
                                    card.getQuestion(),
                                    card.getAnswer(),
                                    card.getDifficulty()
                            ))
                            .toList();

            ctx.json(response);
        });

        // Hent ét spørgsmålskort
        app.get("/api/question-cards/{id}", ctx -> {

            long id;

            try {
                id = Long.parseLong(ctx.pathParam("id"));

                if (id <= 0) {
                    throw new NumberFormatException();
                }

            } catch (NumberFormatException e) {
                ctx.status(400).json(
                        java.util.Map.of(
                                "error",
                                "ID skal være et positivt heltal."
                        )
                );
                return;
            }

            QuestionCard card = questionCardDAO
                    .findById(id)
                    .orElse(null);

            if (card == null) {
                ctx.status(404).json(
                        java.util.Map.of(
                                "error",
                                "Spørgsmålskortet blev ikke fundet."
                        )
                );
                return;
            }

            ctx.json(new QuestionCardDTO(
                    card.getQuestion(),
                    card.getAnswer(),
                    card.getDifficulty()
            ));
        });
    }
}

 */