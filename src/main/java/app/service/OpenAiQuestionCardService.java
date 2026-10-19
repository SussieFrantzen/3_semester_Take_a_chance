package app.service;

import app.dao.QuestionCardDAO;
import app.entities.QuestionCard;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class OpenAiQuestionCardService {

    private static final URI ENDPOINT =
            URI.create("https://api.openai.com/v1/responses");

    private static final String MODEL =
            System.getenv().getOrDefault("OPENAI_MODEL", "gpt-5");

    private static final Set<String> DIFFICULTIES = Set.of(
            "meget let", "let", "middel", "svær"
    );

    private static final String SCHEMA = """
        {
          "type": "object",
          "properties": {
            "questions": {
              "type": "array",
              "minItems": 10,
              "maxItems": 10,
              "items": {
                "type": "object",
                "properties": {
                  "question": {"type": "string"},
                  "answer": {"type": "string"},
                  "difficulty": {
                    "type": "string",
                    "enum": [
                      "meget let",
                      "let",
                      "middel",
                      "svær"
                    ]
                  }
                },
                "required": [
                  "question",
                  "answer",
                  "difficulty"
                ],
                "additionalProperties": false
              }
            }
          },
          "required": ["questions"],
          "additionalProperties": false
        }
        """;

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client;
    private final QuestionCardDAO questionCardDAO;
    private final String apiKey;

    public OpenAiQuestionCardService(
            QuestionCardDAO questionCardDAO) {

        this.questionCardDAO = questionCardDAO;
        this.apiKey = System.getenv("OPENAI_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Miljøvariablen OPENAI_API_KEY mangler."
            );
        }

        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }


    public List<QuestionCard> generateAndSaveCards(
            String subject,
            String ageGroup)
            throws IOException, InterruptedException {

        if (subject == null || subject.isBlank()
                || ageGroup == null || ageGroup.isBlank()) {
            throw new IllegalArgumentException(
                    "Emne og aldersgruppe skal udfyldes."
            );
        }

        subject = subject.strip();
        ageGroup = ageGroup.strip();

        List<GeneratedQuestion> generated =
                generateQuestions(subject, ageGroup);

        List<QuestionCard> savedCards = new ArrayList<>();
        Set<String> batchFingerprints = new HashSet<>();

        for (GeneratedQuestion generatedQuestion : generated) {

            String question = generatedQuestion.question().strip();
            String answer = generatedQuestion.answer().strip();
            String difficulty = generatedQuestion.difficulty();

            if (question.isBlank() || answer.isBlank()
                    || !DIFFICULTIES.contains(difficulty)) {
                throw new IOException(
                        "AI returnerede et ugyldigt spørgsmålskort."
                );
            }

            String fingerprint = createFingerprint(
                    subject, ageGroup, question
            );

            // Undgå gentagelser i samme AI-generering.
            if (!batchFingerprints.add(fingerprint)) {
                continue;
            }

            QuestionCard card = new QuestionCard();
            card.setSubject(subject);
            card.setGroupName("Questencard");
            card.setAgeGroup(ageGroup);
            card.setQuestion(question);
            card.setAnswer(answer);
            card.setDifficulty(difficulty);
            card.setFingerprint(fingerprint);

            // DAO'en kontrollerer også databasen.
            if (questionCardDAO.saveIfNotExists(card)) {
                savedCards.add(card);
            }
        }

        return savedCards;
    }

    private List<GeneratedQuestion> generateQuestions(
            String subject,
            String ageGroup)
            throws IOException, InterruptedException {

        var body = mapper.createObjectNode();

        body.put("model", MODEL);
        body.put("store", false);
        body.put("max_output_tokens", 4000);

        var input = body.putArray("input");

        input.addObject()
                .put("role", "system")
                .put("content", """
                    Du genererer spørgsmålskort til brætspillet
                    Questencard.

                    Skriv på dansk.

                    Spørgsmålene skal være fagligt korrekte,
                    have ét tydeligt korrekt svar og passe til
                    den angivne aldersgruppe.

                    Undgå gentagelser inden for samme generation.

                    Tilpas sværhedsgraden til alderen.
                    Brug konkrete eksempler fra hverdagen,
                    terninger, mønter, kort og sandsynlighed,
                    når emnet lægger op til det.

                    Returner kun data i det angivne JSON-format.
                    """);

        input.addObject()
                .put("role", "user")
                .put("content", """
                    Generer præcis 10 spørgsmålskort.

                    Emne: %s
                    Aldersgruppe: %s

                    Fordel spørgsmålene mellem sværhedsgraderne:
                    meget let, let, middel og svær.

                    Hvert spørgsmål skal have et korrekt svar
                    og én sværhedsgrad.
                    """.formatted(subject, ageGroup));

        var format = body.putObject("text").putObject("format");

        format.put("type", "json_schema");
        format.put("name", "questencard_questions");
        format.put("strict", true);
        format.set("schema", mapper.readTree(SCHEMA));

        HttpRequest request = HttpRequest.newBuilder(ENDPOINT)
                .timeout(Duration.ofSeconds(60))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        mapper.writeValueAsString(body)))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {
            throw new IOException(
                    "OpenAI-request fejlede med HTTP "
                            + response.statusCode() + "."
            );
        }

        JsonNode root = mapper.readTree(response.body());

        if (!"completed".equals(root.path("status").asText())) {
            throw new IOException(
                    "AI-genereringen blev ikke fuldført."
            );
        }

        StringBuilder output = new StringBuilder();

        for (JsonNode item : root.path("output")) {
            if (!"message".equals(item.path("type").asText())) {
                continue;
            }

            for (JsonNode content : item.path("content")) {
                String type = content.path("type").asText();

                if ("refusal".equals(type)) {
                    throw new IOException(
                            "AI afviste at generere spørgsmålskort."
                    );
                }

                if ("output_text".equals(type)) {
                    output.append(
                            content.path("text").asText()
                    );
                }
            }
        }

        if (output.isEmpty()) {
            throw new IOException(
                    "AI-svaret indeholder ingen spørgsmål."
            );
        }

        JsonNode questions = mapper.readTree(
                output.toString()
        ).path("questions");

        if (!questions.isArray() || questions.size() != 10) {
            throw new IOException(
                    "AI skal returnere præcis 10 spørgsmål."
            );
        }

        List<GeneratedQuestion> result = new ArrayList<>();

        for (JsonNode node : questions) {
            String question = node.path("question").asText();
            String answer = node.path("answer").asText();
            String difficulty = node.path("difficulty").asText();

            if (question.isBlank()
                    || answer.isBlank()
                    || !DIFFICULTIES.contains(difficulty)) {
                throw new IOException(
                        "AI returnerede et ugyldigt kort."
                );
            }

            result.add(new GeneratedQuestion(
                    question,
                    answer,
                    difficulty
            ));
        }

        return result;
    }

    private String createFingerprint(
            String subject,
            String ageGroup,
            String question) {

        String normalized = normalize(subject)
                + "|"
                + normalize(ageGroup)
                + "|"
                + normalize(question);

        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(normalized.getBytes(
                            StandardCharsets.UTF_8
                    ));

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "SHA-256 er ikke tilgængelig.", e
            );
        }
    }

    private String normalize(String value) {
        return value.strip()
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }

    private record GeneratedQuestion(
            String question,
            String answer,
            String difficulty) {
    }
}