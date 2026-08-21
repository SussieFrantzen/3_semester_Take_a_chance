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
@Table(name = "game_player")

public class GamePlayer {
    @Id
    private int id;

}
