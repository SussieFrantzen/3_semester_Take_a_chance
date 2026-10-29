package app.service;

import app.entities.FateCard;
import app.entities.QuestionCard;

public record TurnResult(String playerName,
                         String outcome,
                         int movedFields,
                         int position,
                         String landedFieldType,
                         boolean turnContinues,
                         boolean challengeStarted,
                         FateCard fateCard,
                         QuestionCard questionCard,
                         boolean turnMustWait
) {
}