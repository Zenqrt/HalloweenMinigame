package dev.zenqrt.clownchase.game;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public final class ClownChasePlayer implements Audience {

    private @Nullable Player player;

    private @Nullable ClownChaseGame game;
    private final UUID uuid;

    public ClownChasePlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public ClownChasePlayer(@NotNull Player player) {
        this.uuid = player.getUniqueId();
        this.player = player;
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        if (this.player == null)
            return;

        this.player.sendMessage(message);
    }

    @Override
    public void sendActionBar(@NotNull Component message) {
        if (this.player == null)
            return;

        this.player.sendActionBar(message);
    }

    @Override
    public <T> void sendTitlePart(@NotNull TitlePart<T> part, @NotNull T value) {
        if (this.player == null)
            return;

        this.player.sendTitlePart(part, value);
    }

    public void setPlayer(@Nullable Player player) {
        this.player = player;
    }

    public @NotNull Player validatePlayer() {
        return Objects.requireNonNull(player, "ClownChasePlayer.player");
    }

    public @Nullable Player getPlayer() {
        return player;
    }

    public void setGame(@Nullable ClownChaseGame game) {
        this.game = game;
    }

    public @Nullable ClownChaseGame getGame() {
        return game;
    }

    public UUID getUniqueId() {
        return uuid;
    }
}
