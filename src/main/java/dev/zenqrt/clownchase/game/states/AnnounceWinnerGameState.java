package dev.zenqrt.clownchase.game.states;

import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.base.GameState;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.List;

public final class AnnounceWinnerGameState extends GameState {

    private static final String LEADERBOARD_HEADER = "game.leaderboard.header";
    private static final String LEADERBOARD_PLACE = "game.leaderboard.place";
    private static final String LEADERBOARD_PLACE_EMPTY = "game.leaderboard.place_empty";
    private static final String GAME_OVER_TITLE = "game.game_over.title";
    private BukkitTask nextStateTask;
    private final int admirationTime;
    private final ClownChaseGame game;

    public AnnounceWinnerGameState(ClownChaseGame game, int admirationTicks) {
        this.game = game;
        this.admirationTime = admirationTicks;
    }

    @Override
    protected void onStateStart() {
        this.game.getPlayers().forEach((_, gamePlayer) -> gamePlayer.validatePlayer().setGameMode(GameMode.SPECTATOR));

        Audience audience = this.game.audience();
        List<ClownChaseGame.LeaderboardEntry> leaderboard = this.game.getCandyLeaderboard(3);

        audience.sendMessage(leaderboardMessage(leaderboard));
        audience.showTitle(Title.title(
                Component.translatable(GAME_OVER_TITLE, NamedTextColor.RED).decorate(TextDecoration.BOLD),
                Component.empty(),
                Title.Times.times(Duration.ZERO, Duration.ofSeconds(5), Duration.ZERO)
        ));

        this.nextStateTask = Bukkit.getScheduler().runTaskLater(this.game.getPlugin(), this.game::nextState, this.admirationTime);
    }

    @Override
    protected void onStateEnd() {
        if (this.nextStateTask != null && !this.nextStateTask.isCancelled())
            this.nextStateTask.cancel();
    }

    private static Component leaderboardMessage(List<ClownChaseGame.LeaderboardEntry> leaderboard) {
        return Component.empty()
                .append(separator())
                .append(Component.newline())
                .append(Component.translatable(LEADERBOARD_HEADER, NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                .append(Component.text("\n\n"))
                .append(Component.join(
                        JoinConfiguration.builder().separator(Component.newline()),
                        placementMessage(0, Component.text("1. ", NamedTextColor.WHITE).append(Component.text("\uD83D\uDC51", NamedTextColor.GOLD)), leaderboard),
                        placementMessage(1, Component.text("2.", NamedTextColor.WHITE), leaderboard),
                        placementMessage(2, Component.text("3.", NamedTextColor.WHITE), leaderboard)))
                .append(Component.newline())
                .append(separator());
    }

    private static Component placementMessage(int index, Component placementNumber, List<ClownChaseGame.LeaderboardEntry> leaderboard) {

        if (index >= leaderboard.size())
            return Component.translatable(LEADERBOARD_PLACE_EMPTY, NamedTextColor.DARK_GRAY, Component.text(index + 1, NamedTextColor.WHITE));

        ClownChaseGame.LeaderboardEntry entry = leaderboard.get(index);

        return Component.translatable(
                LEADERBOARD_PLACE, NamedTextColor.DARK_GRAY,
                placementNumber,
                Component.text(entry.gamePlayer().validatePlayer().getName(), NamedTextColor.WHITE),
                Component.text(entry.playerData().getCandyCollected(), NamedTextColor.WHITE).append(Component.text(" ♧", NamedTextColor.GREEN)));
    }

    private static Component separator() {
        return Component.text("                                        ", NamedTextColor.GRAY).decorate(TextDecoration.STRIKETHROUGH);
    }
}
