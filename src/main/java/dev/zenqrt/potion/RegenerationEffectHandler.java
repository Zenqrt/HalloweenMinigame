package dev.zenqrt.potion;

import net.minestom.server.entity.Player;
import net.minestom.server.potion.Potion;

public class RegenerationEffectHandler extends PotionEffectHandler {

    public RegenerationEffectHandler(Player player) {
        super(player);
    }

    @Override
    public void onApply(Potion potion) {
        var health = player.getHealth() + potion.getAmplifier();
        if(health <= player.getMaxHealth()) {
            player.setHealth(health);
        }
    }

    @Override
    public void onRemove(Potion potion) {

    }
}
