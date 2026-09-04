package app.entities;


import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@Entity
@Table(name = "player")

public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int points;

    private int age;

    private boolean shieldOfDestiny;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "game_board_id")
    private GameBoard gameBoard;

    public Player(String name, int age, User user, Boolean shieldOfDestiny) {
        this.name = name;
        this.age = age;
        this.user = user;
        this.points = 0;
        this.shieldOfDestiny=shieldOfDestiny;
    }

    public void addPoints(int points) {
        this.points += points;
    }



}
