package dev.zenqrt.game;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.sound.Sound;
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

    @Override
    public void sendMessage(@NotNull Component message) {
        getPlayer().sendMessage(message);
    }

    @Override
    public void sendMessage(@NotNull Identity source, @NotNull Component message) {
        getPlayer().sendMessage(source, message);
    }

    @Override
    public void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
        getPlayer().sendMessage(source, message, type);
    }

    @Override
    public void playSound(@NotNull Sound sound, Sound.@NotNull Emitter emitter) {
        player.playSound(sound, emitter);
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
