package dev.zenqrt.clownchase.exceptions;

import dev.zenqrt.clownchase.game.ClownChasePlayer;

public class GamePlayerAlreadyInGameException extends RuntimeException {

    public GamePlayerAlreadyInGameException(ClownChasePlayer gamePlayer) {
        super(gamePlayer.getPlayer() != null ? gamePlayer.getPlayer().getName() : gamePlayer.getUniqueId().toString() + " is already in the game");
    }
}
