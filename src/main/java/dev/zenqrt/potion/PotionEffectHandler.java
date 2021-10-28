package dev.zenqrt.potion;

import net.minestom.server.entity.Player;
import net.minestom.server.potion.Potion;
import org.jetbrains.annotations.ApiStatus;

public abstract class PotionEffectHandler {

    private final Player player;

    public PotionEffectHandler(Player player) {
        this.player = player;
    }

    public abstract void onApply(Context context);
    public abstract void onRemove(Context context);

    public void tick(Context context) {
    }

    public boolean isTickable() {
        return false;
    }

    @ApiStatus.NonExtendable
    public static class Context {

        private final Player player;
        private final Potion potion;

        @ApiStatus.Internal
        public Context(Player player, Potion potion) {
            this.player = player;
            this.potion = potion;
        }

        public Player getPlayer() {
            return player;
        }

        public Potion getPotion() {
            return potion;
        }
    }

}
