package dev.zenqrt.potion;

import net.minestom.server.entity.Player;
import net.minestom.server.potion.Potion;
import org.jetbrains.annotations.ApiStatus;

public abstract class PotionEffectHandler {

    protected final Player player;

    public PotionEffectHandler(Player player) {
        this.player = player;
    }

    public abstract void onApply(Potion potion);
    public abstract void onRemove(Potion potion);

    public void tick(Potion potion) {
    }

    public boolean isTickable() {
        return false;
    }

}
