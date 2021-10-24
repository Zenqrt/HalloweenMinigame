package dev.zenqrt.entity.projectile;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.EntityProjectile;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityAttackEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CollidingProjectile extends EntityProjectile {

    protected final Player shooter;

    public CollidingProjectile(@Nullable Player shooter, @NotNull EntityType entityType) {
        super(shooter, entityType);
        this.shooter = shooter;

        var node = EventNode.type("colliding-projectile", EventFilter.ENTITY);
        node.addListener(EntityAttackEvent.class, event -> {
            if(!(event.getTarget() instanceof Player target)) return;
            collide(target);
        });
        MinecraftServer.getGlobalEventHandler().map(node, this);
    }

    public abstract void collide(Player player);

    @Override
    public void update(long time) {
        super.update(time);

        if(this.onGround) {
            System.out.println("ground removal");
            this.remove();
        }
    }
}
