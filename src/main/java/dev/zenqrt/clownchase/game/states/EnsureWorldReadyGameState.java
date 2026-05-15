package dev.zenqrt.clownchase.game.states;

import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.base.GameState;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public final class EnsureWorldReadyGameState extends GameState {

    private BukkitTask checkTask;
    private final ClownChaseGame game;

    public EnsureWorldReadyGameState(ClownChaseGame game) {
        this.game = game;
    }

    @Override
    protected void onStateStart() {
        if (this.game.isWorldReady()) {
            this.game.nextState();
            return;
        }

        checkTask = Bukkit.getScheduler().runTaskTimer(this.game.getPlugin(),
                () -> {
                    if (this.game.isWorldReady())
                        this.game.nextState();
                }, 1, 10L);
    }

    @Override
    protected void onStateEnd() {
        if (checkTask != null && !checkTask.isCancelled()) {
            checkTask.cancel();
            checkTask = null;
        }
    }
}
