package dev.zenqrt.clownchase.item.items;

import com.destroystokyo.paper.ParticleBuilder;
import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import dev.zenqrt.clownchase.entity.Clown;
import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.GamePlayerData;
import dev.zenqrt.clownchase.item.CustomItem;
import dev.zenqrt.clownchase.item.CustomItems;
import dev.zenqrt.clownchase.utils.text.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public final class StunBallItem extends CustomItem {

    private static final String HIT_SHIELD_SHOOTER = "game.item.stun_ball.hit_shield.shooter";
    private static final String HIT_NO_SHIELD_SHOOTER = "game.item.stun_ball.hit_no_shield.shooter";
    private static final String HIT_SHIELD_VICTIM = "game.item.stun_ball.hit_shield.victim";
    private static final String HIT_NO_SHIELD_VICTIM = "game.item.stun_ball.hit_no_shield.victim";
    private static final int STUN_DURATION = 60; // 3 seconds

    public StunBallItem() {
        super("stun_ball", Material.SNOWBALL, "Stun Ball", ItemRarity.COMMON);
    }

    public static class Listeners extends CustomItem.Listeners {

        private final List<UUID> projectiles = new ArrayList<>();

        public Listeners(ClownChaseGame game) {
            super(game);
        }

        @Override
        public void unregister() {
            super.unregister();

            this.projectiles.clear();
        }

        @EventHandler
        public void onLaunchProjectile(PlayerLaunchProjectileEvent event) {
            if (!isItemUsedInGame(CustomItems.STUN_BALL, event.getItemStack(), super.game, event.getPlayer().getUniqueId()))
                return;

            this.projectiles.add(event.getProjectile().getUniqueId());
        }

        @EventHandler
        public void onProjectileHit(ProjectileHitEvent event) {
            Projectile projectile = event.getEntity();

            if (!this.projectiles.remove(event.getEntity().getUniqueId()))
                return;

            if (!(projectile.getShooter() instanceof Player shooter))
                return;

            if (event.getHitEntity() instanceof Player hit) {
                UUID hitUuid = event.getHitEntity().getUniqueId();

                if (!super.game.hasPlayer(hitUuid))
                    return;

                GamePlayerData hitPlayerData = super.game.getPlayerData(hitUuid);

                if (hitPlayerData.isShielded()) {
                    super.game.breakShield(hit, hitPlayerData);

                    shooter.sendMessage(Component.translatable(HIT_SHIELD_SHOOTER, NamedTextColor.YELLOW,
                            Messages.username(hit)
                    ));
                    hit.sendMessage(Component.translatable(HIT_SHIELD_VICTIM, NamedTextColor.RED,
                            Messages.username(shooter)
                    ));
                } else {
                    hit.addPotionEffects(createStunEffect(STUN_DURATION));
                    createHitParticle()
                            .location(projectile.getLocation())
                            .spawn();

                    shooter.sendMessage(Component.translatable(HIT_NO_SHIELD_SHOOTER, NamedTextColor.YELLOW,
                            Messages.username(hit), Messages.seconds(STUN_DURATION / 20)));
                    hit.sendMessage(Component.translatable(HIT_NO_SHIELD_VICTIM, NamedTextColor.YELLOW,
                            Messages.username(shooter), Messages.seconds(STUN_DURATION / 20)));
                }
            } else if (event.getHitEntity() instanceof Husk hitEntity) {
                Clown clown = super.game.getPlayerToClown().get(shooter.getUniqueId());

                if (clown == null || !clown.getUUID().equals(hitEntity.getUniqueId()))
                    return;

                shooter.sendMessage("Not yet.");
            }
        }

        private static Collection<PotionEffect> createStunEffect(int duration) {
            return List.of(
                    new PotionEffect(PotionEffectType.SLOWNESS, duration, 255, true, true, true),
                    new PotionEffect(PotionEffectType.JUMP_BOOST, duration, 200, true, true, true)
            );
        }

        private static ParticleBuilder createHitParticle() {
            return Particle.FIREWORK.builder()
                    .count(10)
                    .extra(0.5);
        }
    }


}
