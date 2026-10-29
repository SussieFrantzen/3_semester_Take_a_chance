package app.service;

import app.entities.Player;
import java.util.List;

class ChallengeState {

    final List<Player> turnOrder;
    final List<Boolean> bag;
    final int initialCoconuts;

    int currentPlayerIndex;
    boolean finished;

    ChallengeState(
            List<Player> turnOrder,
            List<Boolean> bag,
            int initialCoconuts) {

        this.turnOrder = turnOrder;
        this.bag = bag;
        this.initialCoconuts = initialCoconuts;
        this.currentPlayerIndex = 0;
        this.finished = false;
    }

    boolean isFinished() {
        return finished;
    }
}