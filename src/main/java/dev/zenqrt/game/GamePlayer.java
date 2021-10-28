package dev.zenqrt.game;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class GamePlayer implements Audience {

    private final Player player;
    private Game currentGame;

    @ApiStatus.Internal
    public GamePlayer(Player player) {
        this.player = player;
    }

    @Override
    public void sendActionBar(@NotNull Component message) {
        player.sendActionBar(message);
    }

    @Override
    public void showTitle(@NotNull Title title) {
        player.showTitle(title);
    }

    public Player getPlayer() {
        return player;
    }

    public void setCurrentGame(Game game) {
        this.currentGame = game;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

}
