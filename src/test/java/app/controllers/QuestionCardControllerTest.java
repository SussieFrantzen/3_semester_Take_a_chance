/*package app.dao;

import app.config.HibernateTestConfig;
import app.entities.QuestionCard;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionCardDAOTest {

    private static final EntityManagerFactory emf =
            HibernateTestConfig.getEntityManagerFactory();

    private QuestionCardDAO questionCardDAO;

    @BeforeEach
    void setUp() {
        questionCardDAO = new QuestionCardD(emf);
    }

    @AfterAll
    static void shutdown() {
        HibernateTestConfig.close();
    }

    @Test
    void findBySubjectAndAgeGroupReturnsMatchingCards() {

        QuestionCard card = createQuestionCard(
                "Matematik",
                "6-9",
                "Hvad er 2 + 2?",
                "4",
                "EASY",
                "test-fingerprint-1"
        );

        assertTrue(questionCardDAO.saveIfNotExists(card));

        List<QuestionCard> result =
                questionCardDAO.findBySubjectAndAgeGroup(
                        "Matematik",
                        "6-9"
                );

        assertNotNull(result);
        assertFalse(result.isEmpty());

        assertTrue(
                result.stream().anyMatch(q ->
                        q.getQuestion().equals("Hvad er 2 + 2?")
                )
        );
    }

    @Test
    void saveIfNotExistsSavesNewCard() {

        QuestionCard card = createQuestionCard(
                "Natur",
                "10-12",
                "Hvad hedder vores planet?",
                "Jorden",
                "EASY",
                "test-fingerprint-2"
        );

        boolean result =
                questionCardDAO.saveIfNotExists(card);

        assertTrue(result);
        assertNotNull(card.getId());
    }

    @Test
    void saveIfNotExistsDoesNotSaveDuplicateFingerprint() {

        QuestionCard firstCard = createQuestionCard(
                "Historie",
                "13-15",
                "Hvornår sluttede 2. verdenskrig?",
                "1945",
                "MEDIUM",
                "test-fingerprint-3"
        );

        QuestionCard duplicateCard = createQuestionCard(
                "Historie",
                "13-15",
                "Hvornår sluttede 2. verdenskrig?",
                "1945",
                "MEDIUM",
                "test-fingerprint-3"
        );

        assertTrue(
                questionCardDAO.saveIfNotExists(firstCard)
        );

        assertFalse(
                questionCardDAO.saveIfNotExists(duplicateCard)
        );
    }

    private QuestionCard createQuestionCard(
            String subject,
            String ageGroup,
            String question,
            String answer,
            String difficulty,
            String fingerprint) {

        QuestionCard card = new QuestionCard();

        card.setSubject(subject);
        card.setAgeGroup(ageGroup);
        card.setQuestion(question);
        card.setAnswer(answer);
        card.setDifficulty(difficulty);
        card.setFingerprint(fingerprint);

        return card;
    }
}

 */