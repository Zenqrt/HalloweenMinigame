package dev.zenqrt.game;

import dev.zenqrt.game.ending.Ending;

import java.util.ArrayList;
import java.util.List;

public abstract class Game {

    private final List<GamePlayer> players;
    private final GameOptions gameOptions;
    private final int id;

    protected GameState state;

    public Game(int id, GameOptions gameOptions) {
        this.id = id;
        this.gameOptions = gameOptions;
        this.players = new ArrayList<>();
        this.state = GameState.WAITING;
    }

    public abstract void startGame();
    public abstract void endGame(Ending ending);

    public List<GamePlayer> getPlayers() {
        return players;
    }

    public GameOptions getGameOptions() {
        return gameOptions;
    }

    public int getId() {
        return id;
    }
}
