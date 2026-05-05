package dev.zenqrt.clownchase.game.states;

import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.base.GameState;

public final class ChaseGameState extends GameState {

    private final ClownChaseGame game;

    public ChaseGameState(ClownChaseGame game) {
        this.game = game;
    }

    @Override
    protected void onStateStart() {
        this.game.getClowns().forEach((_, clown) -> clown.setNoAi(false));
    }
}
