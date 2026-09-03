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
    private List<Player> players = new ArrayList<>();

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Field> fields = new ArrayList<>();

    @OneToOne(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private CardDeck cardDeck;

    @OneToOne(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )

    private Coin coin;

    public void addPlayer(Player player) {

        if (players.size() >= 4) {
            throw new IllegalStateException(
                    "maks 4 spiller"
            );
        }
        if(players.isEmpty()){
            throw new IllegalStateException(
                    "Der skal minimum være 1 spiller");
        }

        players.add(player);
        player.setGameBoard(this);
    }



}
