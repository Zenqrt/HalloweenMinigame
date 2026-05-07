package dev.zenqrt.clownchase.game.base;

import java.util.List;

public class GameStateSequence extends GameState {

    protected List<GameState> states;
    private int stateIndex;

    @Override
    protected void onStateStart() {
        this.states.get(this.stateIndex).start();
    }

    @Override
    protected void onStateEnd() {
        this.states.get(this.stateIndex).end();
    }

    public final void nextState() {
        if (this.stateIndex + 1 >= this.states.size()) {
            this.end();
            return;
        }

        GameState currentState = this.states.get(this.stateIndex);
        currentState.end();

        this.states.get(++this.stateIndex).start();
    }

    public final void previousState() {
        if (this.stateIndex - 1 < 0)
            throw new IndexOutOfBoundsException("state index below 0");

        GameState currentState = this.states.get(this.stateIndex);
        currentState.end();

        this.states.get(--this.stateIndex).start();
    }

    public GameState getCurrentState() {
        return this.states.get(this.stateIndex);
    }
}
