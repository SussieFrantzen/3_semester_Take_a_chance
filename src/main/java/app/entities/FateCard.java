package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fate_cards")
@Getter
@Setter
@NoArgsConstructor
public class FateCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_deck_id", nullable = false)
    private CardDeck cardDeck;

    @Column(length = 1000)
    private String text;

    private String groupName;

    private String action;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FateCardType type = FateCardType.NORMAL;

    public FateCard(
            String text,
            String groupName,
            String action,
            CardDeck cardDeck) {

        this.text = text;
        this.groupName = groupName;
        this.action = action;
        this.cardDeck = cardDeck;
        this.type = FateCardType.NORMAL;
    }

    public FateCard(
            String text,
            String groupName,
            String action,
            FateCardType type,
            CardDeck cardDeck) {

        this.text = text;
        this.groupName = groupName;
        this.action = action;
        this.type = type;
        this.cardDeck = cardDeck;
    }
}