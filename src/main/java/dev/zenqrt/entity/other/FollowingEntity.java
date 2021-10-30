package dev.zenqrt.entity.other;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class FollowingEntity extends Entity {

    private final Entity entity;
    public Vec offset;

    public FollowingEntity(@NotNull EntityType entityType, @NotNull UUID uuid, Entity entity, Vec offset) {
        super(entityType, uuid);
        this.entity = entity;
        this.offset = offset;
    }

    public FollowingEntity(@NotNull EntityType entityType, Entity entity, Vec offset) {
        this(entityType, UUID.randomUUID(), entity, offset);
    }

    public FollowingEntity(@NotNull EntityType entityType, Entity entity) {
        this(entityType, UUID.randomUUID(), entity, Vec.ZERO);
    }

    @Override
    public void tick(long time) {
        var instance = entity.getInstance();
        if(instance != null) {
            if(this.instance == null) {
                this.setInstance(instance, position);
            }
        }
        
        super.tick(time);
    }

    @Override
    public void update(long time) {
        super.update(time);

        if(entity == null || entity.isRemoved()) {
            this.remove();
            return;
        }

        var position = entity.getPosition().add(offset);
        if(instance != this.getInstance()) {
            this.setInstance(instance, position);
        }
        teleport(position);
    }

    public Entity getEntity() {
        return entity;
    }
}
