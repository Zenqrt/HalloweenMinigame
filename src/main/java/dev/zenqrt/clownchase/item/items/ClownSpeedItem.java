package dev.zenqrt.clownchase.item.items;

import com.destroystokyo.paper.ParticleBuilder;
import dev.zenqrt.clownchase.entity.Clown;
import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.item.CustomItem;
import dev.zenqrt.clownchase.item.CustomItems;
import dev.zenqrt.clownchase.utils.text.Messages;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;

public final class ClownSpeedItem extends CustomItem {

    private static final String BUFF_VICTIM = "game.item.clown_speed.buff_victim";
    private static final String BUFF_SOURCE = "game.item.clown_speed.buff_source";
    private static final Sound BUFF_SOUND = Sound.sound(Key.key("minecraft:block.beacon.activate"), Sound.Source.MASTER, 1, 2);
    private static final int SPEED_TIME = 10;  // seconds

    public ClownSpeedItem() {
        super("clown_speed", Material.FEATHER, "Clown Fast", ItemRarity.EPIC);
    }

    public static class Listeners extends CustomItem.Listeners {

        public Listeners(ClownChaseGame game) {
            super(game);
        }

        @EventHandler
        public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
            if (event.getHand() != EquipmentSlot.HAND)
                return;

            Player player = event.getPlayer();
            ItemStack itemStack = player.getInventory().getItemInMainHand();

            if (!(isItemUsedInGame(CustomItems.CLOWN_SPEED, itemStack, super.game, player.getUniqueId())))
                return;

            if (!(((CraftEntity) event.getRightClicked()).getHandle() instanceof Clown clown))
                return;

            clown.addEffect(new MobEffectInstance(MobEffects.SPEED, SPEED_TIME * 20, 1, true, true));

            super.game.getGameWorld().playSound(BUFF_SOUND, clown.getX(), clown.getY(), clown.getZ());
            createBuffParticles()
                    .location(event.getRightClicked().getLocation())
                    .spawn();

            player.sendMessage(Component.translatable(BUFF_SOURCE, NamedTextColor.YELLOW,
                    Messages.username((Player) clown.getPlayerTarget().getBukkitEntity()),
                    Component.text("Speed I", NamedTextColor.AQUA),
                    Messages.seconds(SPEED_TIME)
            ));
            clown.getPlayerTarget().getBukkitEntity().sendMessage(Component.translatable(BUFF_VICTIM, NamedTextColor.YELLOW,
                    Messages.username(player),
                    Component.text("Speed I", NamedTextColor.AQUA),
                    Messages.seconds(SPEED_TIME)
            ));

            ((CraftItemStack) itemStack).handle.consume(1, null);
        }

        private static ParticleBuilder createBuffParticles() {
            return Particle.SCULK_SOUL.builder()
                    .offset(0.25, 0.5, 0.25)
                    .count(10)
                    .extra(0);
        }
    }
}
