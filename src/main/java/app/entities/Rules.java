package app.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Rules {

    private String introduction;
    private String ruleSet;

    public void showIntroduction() {
        // TODO show the rules for the game
    }

    public void showRules() {
        // TODO
    }

    }