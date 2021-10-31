package dev.zenqrt.item.powerup;

import dev.zenqrt.item.CustomItem;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.sound.SoundEvent;

public class CandyMagnetItem extends CustomItem {

    public CandyMagnetItem() {
        super("candy_magnet", ItemStack.builder(Material.COMPASS)
                .displayName(Component.text("Candy Magnet")
                        .decoration(TextDecoration.ITALIC, false)
                ).lore(Component.text("Attracts candies to you in a 3 block radius.", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false))
                .build()
        );

        createPlayerListener(event -> event.getPlayer().getItemInMainHand(), PlayerUseItemEvent.class, event -> {
            var player = event.getPlayer();
            player.playSound(Sound.sound(SoundEvent.BLOCK_BEACON_ACTIVATE, Sound.Source.PLAYER, 1, 1), Sound.Emitter.self());
            player.addEffect(new Potion(PotionEffect.CONDUIT_POWER, (byte) 1, 300));

        });
    }
}
