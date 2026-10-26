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

import app.entities.CardDeck;

public class OpenAiQuestionCardService {

    private static final URI ENDPOINT =
            URI.create("https://api.openai.com/v1/responses");

    private static final String MODEL =
            System.getenv().getOrDefault("OPENAI_MODEL", "gpt-5");

    private static final Set<String> DIFFICULTIES = Set.of(
            "meget let", "let", "middel", "svær"
    );

    private String createSchema(int amountToGenerate) {
        return """
                {
                  "type": "object",
                  "properties": {
                    "questions": {
                      "type": "array",
                      "minItems": %d,
                      "maxItems": %d,
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
                """.formatted(amountToGenerate, amountToGenerate);
    }

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client;
    private final QuestionCardDAO questionCardDAO;
    private final String apiKey;

    public OpenAiQuestionCardService(QuestionCardDAO questionCardDAO) {

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


    public List<QuestionCard> generateAndSaveCards(String subject, String ageGroup, CardDeck deck, int amountToGenerate
    ) throws IOException, InterruptedException {
        if (amountToGenerate < 1 || amountToGenerate > 10) {
            throw new IllegalArgumentException(
                    "Antallet af spørgsmål skal være mellem 1 og 10."
            );
        }

        if (subject == null || subject.isBlank()
                || ageGroup == null || ageGroup.isBlank()) {

            throw new IllegalArgumentException(
                    "Emne og aldersgruppe skal udfyldes."
            );
        }
        if (deck == null) {
            throw new IllegalArgumentException(
                    "Kortbunken skal angives."
            );
        }

        subject = subject.strip();
        ageGroup = ageGroup.strip();

        List<GeneratedQuestion> generated = generateQuestions(subject, ageGroup, amountToGenerate);

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

            if (!batchFingerprints.add(fingerprint)) { // Undgå gentagelser i samme AI-generering.
                continue;
            }

            QuestionCard card = new QuestionCard();
            card.setCardDeck(deck);
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

    private List<GeneratedQuestion> generateQuestions(String subject, String ageGroup, int amountToGenerate)
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
                        
                         STIL OG FORMULERING:
                         - Bland genkendelige hverdagssituationer med små gåder
                           og tankevækkende udfordringer.
                         - Brug fx situationer fra spil, skole, indkøb, fritid,
                           venner og familie, når de passer til emnet.
                         - Formuler spørgsmålene naturligt og levende, så spilleren
                           får lyst til at finde svaret.
                         - Nogle spørgsmål må gerne tage udgangspunkt i en
                           hverdagssituation, mens andre gerne må være korte gåder.
                         - Brug let humor eller en overraskende vinkel, når det
                           falder naturligt, men undgå tvungne vittigheder.
                         - Undgå lange historier og unødvendige detaljer.
                         - Tilpas sprog, scenarier og sværhedsgrad til aldersgruppen.
                         - Sørg for, at scenariet hjælper med at forstå opgaven
                           og ikke gør den mere forvirrende.
                         - Alle spørgsmål skal være fagligt korrekte og have
                           ét tydeligt korrekt svar.
                         - Hvis et spørgsmål bygger på en antagelse, skal den
                           fremgå tydeligt.
                        
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
                        Generer præcis %d spørgsmålskort.
                        
                        Emne: %s
                        Aldersgruppe: %s
                        
                            Aldersgrupperne betyder:
                            - 6-9: børn fra 6 til og med 9 år
                            - 10-12: børn fra 10 til og med 12 år
                            - 13-15: unge fra 13 til og med 15 år
                            - 16+: personer på 16 år eller derover
                        
                            Tilpas ordforråd, kompleksitet og nødvendige forkundskaber
                            til den angivne aldersgruppe.
                        
                        Fordel spørgsmålene mellem sværhedsgraderne:
                        meget let, let, middel og svær.
                        
                        Hvert spørgsmål skal have et korrekt svar
                        og én sværhedsgrad.
                      """.formatted(amountToGenerate, subject, ageGroup));

        var format = body.putObject("text").putObject("format");

        format.put("type", "json_schema");
        format.put("name", "questencard_questions");
        format.put("strict", true);
        format.set("schema", mapper.readTree(createSchema(amountToGenerate)));

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

        if (!questions.isArray()
                || questions.size() != amountToGenerate) {
            throw new IOException(
                    "AI skal returnere præcis "
                            + amountToGenerate
                            + " spørgsmål."
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