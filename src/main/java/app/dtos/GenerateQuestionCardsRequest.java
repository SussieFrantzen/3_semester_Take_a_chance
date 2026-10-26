package app.dtos;

public record GenerateQuestionCardsRequest(
        String subject,
        String ageGroup,
        Long cardDeckId
) {
}