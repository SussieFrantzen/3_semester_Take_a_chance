package app.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Coin {

    private boolean heads;

    public String flip() {
        heads = Math.random() < 0.5;
        return heads ? "Heads" : "Tails";
    }
}
