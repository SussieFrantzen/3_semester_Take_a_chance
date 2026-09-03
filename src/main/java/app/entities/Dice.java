package app.entities;

import lombok.Getter;

@Getter
public class Dice {

    private int value;

    public int roll(){
        // TODO make a dice turning between 1-6 and show up on the screen and return the value of the dice result.
        return value;
    }
}
