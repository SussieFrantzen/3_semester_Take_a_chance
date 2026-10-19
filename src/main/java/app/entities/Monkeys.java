package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "monkeys")
@Getter
@Setter
@NoArgsConstructor
public class Monkeys {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int coconuts;

    private int bananas;

    private int missCards = 1;

    public Monkeys(int coconuts, int bananas) {
        this.coconuts = coconuts;
        this.bananas = bananas;
        this.missCards = 1;
    }

    public void banana() {
        bananas++;
    }

    public void coconut() {
        coconuts++;
    }

    public void addMissCard() {
        missCards++;
    }
}