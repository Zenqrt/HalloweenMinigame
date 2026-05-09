package dev.zenqrt.clownchase.entity.candy;

import com.destroystokyo.paper.ParticleBuilder;
import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.GamePlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.world.level.Level;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public final class ShieldCandy extends SpecialCandy {

    private static final String CANDY_TITLE = "game.candy.shield.title";
    private static final String TEXTURES = "ewogICJ0aW1lc3RhbXAiIDogMTYzNTExMTMxNTAwOSwKICAicHJvZmlsZUlkIiA6ICI5NDA5NDM2ZDVmYjE0NjA3ODI3OTU3YTY4MWZiMGU1MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNYXhCWmlnIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2QxMDgxODMwZTY0NGQyOWVlMTk0YmQ2NWU3NDA5OWZiOTc0N2RhOGMzZDg4MTdmOTQ4MjllNjVjZmNlNDlhIgogICAgfQogIH0KfQ==";

    public ShieldCandy(Level level) {
        super(level, Component.translatable(CANDY_TITLE, TextColor.color(0xD7BAFF)).decorate(TextDecoration.BOLD), TEXTURES);
    }

    @Override
    protected void particleTick() {
        createSpecialParticle()
                .location(
                        this.level().getWorld(),
                        this.getX(),
                        this.getY() - 0.25,
                        this.getZ()
                ).spawn();
    }

    private static ParticleBuilder createSpecialParticle() {
        return Particle.REVERSE_PORTAL.builder()
                .offset(0.25, 0.25, 0.25)
                .extra(0)
                .count(1);
    }

    @Override
    public void onConsume(ClownChaseGame game, GamePlayerData playerData, Player player) {
        game.grantShield(player, playerData);
    }
}
