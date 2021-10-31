package dev.zenqrt.potion;

import net.minestom.server.entity.Player;
import net.minestom.server.potion.Potion;

public class EmptyEffectHandler extends PotionEffectHandler {

    public EmptyEffectHandler(Player player) {
        super(player);
    }

    @Override
    public void onApply(Potion potion) {

    }

    @Override
    public void onRemove(Potion potion) {

    }
}
