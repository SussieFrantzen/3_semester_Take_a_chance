package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game_boards")
@Getter
@Setter
@NoArgsConstructor
public class GameBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(
            mappedBy = "gameBoard",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderColumn(name = "field_order")
    private List<Field> fields = new ArrayList<>();

    @OneToMany(mappedBy = "gameBoard")
    private List<Player> players = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_name_id", nullable = false)
    private GameName gameName;

    @ManyToOne
    @JoinColumn(name = "card_deck_id")
    private CardDeck cardDeck;


    @Column(nullable = false)
    private String boardName;

    @Column(nullable = false)
    private String subject;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardLength boardLength;


}
