package dev.zenqrt.clownchase.exceptions;

import dev.zenqrt.clownchase.game.ClownChaseGame;

public class GameAlreadyFullException extends RuntimeException {
    public GameAlreadyFullException(ClownChaseGame game) {
        super("Game " + game.getId() + " is already full");
    }
}
