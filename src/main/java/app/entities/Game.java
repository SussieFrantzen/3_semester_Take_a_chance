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

    @OneToOne
    private GameBoard gameBoard;

    @OneToMany(
            mappedBy = "game",
            cascade = CascadeType.ALL
    )
    @OrderBy("id ASC") // stigende rækkefølge
    private List<Player> players = new ArrayList<>();


    @Column(nullable = false)
    private int currentPlayerIndex = 0; // for at gemme index af hvilken spiller der har tur.


    @Column(nullable = false)
    private int goCount = 0; // Go er når spillerens trykker på en knap for at se om de må rykke eller skal blive stående i deres tur.

    public Player getCurrentPlayer() {  // metode til at finde den spiller der har tur
        if (players.isEmpty()) {
            throw new IllegalStateException(
                    "Spillet har ingen spillere."
            );
        }

        return players.get(currentPlayerIndex); // returner den spiller hvis tur det er
    }

    public void addPlayer(Player player) {
        if (players.size() >= 4) { // tjekker antallet af tilknyttet spiller til aktuel spil. Hvis der er 4 spiller kan der ikke tilføjes flere
            throw new IllegalStateException(
                    "Der må højst være 4 spillere."
            );
        }

        player.setGame(this); // her forbindes spilleren til det aktuelle spil
        player.setGameBoard(gameBoard); // spilbræt tilknyttes til spilleren.
        players.add(player); // tilføjer spilleren til spillet
    }

    public void endTurn() {
        if (players.isEmpty()) {
            return;
        }
        currentPlayerIndex =(currentPlayerIndex + 1) % players.size();
        // når den nuværende spiller er færdig med sin tur bliver det næste og når alle deltager har været igennem starter den forfra ved 0
    }


}