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

    public Monkeys(int coconuts, int bananas) {
        this.coconuts = coconuts;
        this.bananas = bananas;
    }

    public void play() {
        // TODO
    }

    public void banana() {
        // TODO
    }

    public void coconut() {
        // TODO
    }
}

