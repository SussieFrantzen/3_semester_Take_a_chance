package app.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @OneToOne
    private GameBoard gameboard;

    @OneToMany(mappedBy = "game")
    private List<Player> players = new ArrayList<>();

}
