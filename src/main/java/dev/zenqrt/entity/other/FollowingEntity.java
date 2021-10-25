package dev.zenqrt.entity.other;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class FollowingEntity extends Entity {

    private final Vec offset;
    private final Entity entity;

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
    public void update(long time) {
        super.update(time);

        if(entity == null || entity.isRemoved()) {
            this.remove();
            return;
        }

        var instance = entity.getInstance();
        if(instance == null) {
            return;
        }

        var position = entity.getPosition().add(offset);
        if(instance != this.getInstance()) {
            this.setInstance(instance, position);
        }
        teleport(position);
    }
}
