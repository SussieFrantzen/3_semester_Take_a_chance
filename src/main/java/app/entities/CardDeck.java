package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "card_decks")
@Getter
@Setter
@NoArgsConstructor
public class CardDeck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionCard> questionCards = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FateCard> fateCards = new ArrayList<>();

    public void addQuestionCard(QuestionCard card) {
        if (card != null && !questionCards.contains(card)) {
            questionCards.add(card);
        }
    }

    public QuestionCard drawQuestionCard(String ageGroup) {
        List<QuestionCard> matchingCards = questionCards.stream()
                .filter(card -> ageGroup.equals(card.getAgeGroup()))
                .toList();

        if (matchingCards.isEmpty()) {
            return null;
        }

        int index = ThreadLocalRandom.current()
                .nextInt(matchingCards.size());

        return matchingCards.get(index);
    }
}