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

    @Column(length = 1000)
    private String text;

    private String groupName;

    private String action;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FateCardType type = FateCardType.NORMAL;

    @OneToOne(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Monkeys monkeys;

    public FateCard(
            String text,
            String groupName,
            String action) {

        this.text = text;
        this.groupName = groupName;
        this.action = action;
        this.type = FateCardType.NORMAL;
    }

    public FateCard(
            String text,
            String groupName,
            String action,
            FateCardType type) {

        this.text = text;
        this.groupName = groupName;
        this.action = action;
        this.type = type;
    }
}