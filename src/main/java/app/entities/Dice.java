package app.entities;

import lombok.Getter;

import java.util.Random;

@Getter
public class Dice {

    private int value;

    public int roll(){
        value = new Random().nextInt(6)+1;
        return value;
    }
}
