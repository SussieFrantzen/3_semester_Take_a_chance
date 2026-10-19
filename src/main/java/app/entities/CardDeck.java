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

    public void addQuestionCard(QuestionCard card) { // tilføj spørgsmålskort
        if (card != null && !questionCards.contains(card)) { // hvis spørgsmålskortet ikke er null og kortet ikke eksistere i forvejen i listen/databasen over spørgsmålskort
            questionCards.add(card); // tilføjes det nye spørgsmålskort.
        }
    }

    public QuestionCard drawQuestionCard(String ageGroup) {  // Træk Spørgsmålskort
        List<QuestionCard> matchingCards = questionCards.stream() // opretter en liste som leder igennem spørgsmålskortene og filtrer med stream
                .filter(card -> ageGroup.equals(card.getAgeGroup())) // filteret sortere ud fra alders gruppe. så listen kun indeholder kort der passer der til.
                .toList(); // tilføjer disse kort til listen.

        if (matchingCards.isEmpty()) { // hvis der ikke er nogle kort der matcher
            return null;  // returneres null  /// TODO sørg for at spillernes alder indtastning ikke kan give udslag.
        }

        int index = ThreadLocalRandom.current()
                .nextInt(matchingCards.size()); // vælger et tilfældigt index ud fra listens størrelse.

        return matchingCards.get(index); // returnere det kort der matcher index
    }

    public FateCard drawFateCard() { // træk Skæbnekort
        if (fateCards == null || fateCards.isEmpty()) { // hvis skæbnekort ikke findes eller skæbnekort bunken er tom returneres null.
            System.out.println("der er ikke nogle skæbnekort"); // kontrol besked
        }
        else {
            System.out.println("Der er trukket et skæbnekort"); // kontrol besked
        }

        int index = ThreadLocalRandom.current() // tilfældigt index findes ud fra mængden af skæbnekort der er.
                .nextInt(fateCards.size());

        return fateCards.get(index); // returnere skæbnekortet der matcher index.
    }
}