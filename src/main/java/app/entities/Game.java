package app.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_board_id", nullable = false)
    private GameBoard gameBoard;

    @OneToMany(
            mappedBy = "game",
            cascade = CascadeType.ALL
    )
    @OrderBy("id ASC")
    private List<Player> players = new ArrayList<>();


    @Column(nullable = false)
    private int currentPlayerIndex = 0;


    @Column(nullable = false)
    private int goCount = 0;



    public Player getCurrentPlayer() {
        if (players.isEmpty()) {
            throw new IllegalStateException(
                    "Spillet har ingen spillere."
            );
        }

        return players.get(currentPlayerIndex);
    }

    public void addPlayer(Player player) {
        if (players.size() >= 4) {
            throw new IllegalStateException(
                    "Der må højst være 4 spillere."
            );
        }

        player.setGame(this);
        player.setGameBoard(gameBoard);
        players.add(player);
    }

    public void endTurn() {
        if (players.isEmpty()) {
            return;
        }
        currentPlayerIndex =(currentPlayerIndex + 1) % players.size();
    }


}