package app.service;

import app.dao.QuestionCardDAO;
import app.entities.CardDeck;
import app.entities.Field;
import app.entities.GameBoard;
import app.entities.Player;
import app.entities.QuestionCard;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class QuestencardService {

    private final QuestionCardDAO questionCardDAO;
    private final AgeGroupService ageGroupService;
    private final OpenAiQuestionCardService aiService;

    public QuestencardService(
            QuestionCardDAO questionCardDAO,
            AgeGroupService ageGroupService,
            OpenAiQuestionCardService aiService) {

        this.questionCardDAO = questionCardDAO;
        this.ageGroupService = ageGroupService;
        this.aiService = aiService;
    }

    public QuestionCard activate(Field field, Player player, GameBoard board)
            throws IOException, InterruptedException {

        if (field == null
                || !"QUESTIONFIELD".equals(field.getType())) {
            return null;
        }

        String ageGroup =
                ageGroupService.getAgeGroup(player.getAge());

        String subject = board.getSubject();

        if (subject == null || subject.isBlank()) {
            throw new IllegalStateException(
                    "Spillebanen mangler et emne."
            );
        }


        List<QuestionCard> cards =
                questionCardDAO.findBySubjectAndAgeGroup(
                        subject, ageGroup);



        CardDeck deck = board.getCardDeck();

        if (deck == null) {
            throw new IllegalStateException(
                    "Spillebanen mangler en kortbunke."
            );
        }


        if (cards.isEmpty()) {
            aiService.generateAndSaveCards(
                    subject,
                    ageGroup,
                    deck,
                    10
            );

            aiService.generateAndSaveCards(
                    subject,
                    ageGroup,
                    deck,
                    10
            );

            cards = questionCardDAO.findBySubjectAndAgeGroup(
                    subject, ageGroup);
        }

        if (cards.isEmpty()) {
            throw new IllegalStateException(
                    "Der kunne ikke findes eller genereres spørgsmål."
            );
        }



        QuestionCard selected = cards.get(
                ThreadLocalRandom.current().nextInt(cards.size())
        );

        return selected;
    }
}