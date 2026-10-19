package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int points;

    private int age;

    private boolean shieldOfDestiny;


    @Column(nullable = false)
    private int position = 0;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "game_board_id")
    private GameBoard gameBoard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    public Player(String name, int age, User user) {
        this.name = name;
        this.age = age;
        this.user = user;
        this.points = 0;
        this.shieldOfDestiny = false;
        this.position = 0;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public boolean hasShieldOfDestiny() {
        return shieldOfDestiny;
    }

    public void activateShieldOfDestiny() {
        this.shieldOfDestiny = true;
    }

    public void useShieldOfDestiny() {
        this.shieldOfDestiny = false;
    }

}