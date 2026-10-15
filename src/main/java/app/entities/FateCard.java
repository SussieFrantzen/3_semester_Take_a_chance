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

    @Column(length = 1000) // made the text length to 1000
    private String text;

    private String groupName;

    private String action;

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
    }

}
