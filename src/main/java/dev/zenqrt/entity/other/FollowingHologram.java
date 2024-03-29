package dev.zenqrt.entity.other;

import net.kyori.adventure.text.Component;
import net.minestom.server.Viewable;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class FollowingHologram implements Viewable {
    private static final float OFFSET_Y = -0.9875F;
    private static final float MARKER_OFFSET_Y = -0.40625F;
    private final FollowingEntity entity;

    private Vec offset;
    private Component text;
    private boolean removed;

    public FollowingHologram(Entity entity, Component text, Vec offset) {
        this(entity, text, offset, true);
    }

    public FollowingHologram(Entity entity, Component text, Vec offset, boolean autoViewable) {
        this(entity, text, offset, autoViewable, false);
    }

    public FollowingHologram(Entity entity, Component text, Vec offset, boolean autoViewable, boolean marker) {
        this.entity = new FollowingEntity(EntityType.ARMOR_STAND, entity, offset);
        System.out.println("ENTITY INSTANCE NULL " + (entity.getInstance() == null));
        createArmorStand(entity.getInstance(), entity.getPosition(), autoViewable, marker);
        this.setText(text);
    }

    private void createArmorStand(Instance instance, Point spawnPosition, boolean autoViewable, boolean marker) {
        var armorStandMeta = (ArmorStandMeta) this.entity.getEntityMeta();
        armorStandMeta.setNotifyAboutChanges(false);
        if (marker) {
            this.offset = entity.offset.add(0, MARKER_OFFSET_Y, 0);
            armorStandMeta.setMarker(true);
        } else {
            this.offset = entity.offset.add(0, OFFSET_Y, 0);
            armorStandMeta.setSmall(true);
        }

        armorStandMeta.setHasNoGravity(true);
        armorStandMeta.setCustomName(Component.empty());
        armorStandMeta.setCustomNameVisible(true);
        armorStandMeta.setInvisible(true);
        armorStandMeta.setNotifyAboutChanges(true);

        if(instance != null) {
            this.entity.setInstance(instance, spawnPosition.add(offset));
        }
//        this.entity.setAutoViewable(autoViewable);
    }

    public Pos getPosition() {
        return this.entity.getPosition().add(offset);
    }

    public void setOffset(Vec offset) {
        this.offset = offset;
        this.entity.offset = offset;
    }

    public Vec getOffset() {
        return offset;
    }

    public Component getText() {
        return this.text;
    }

    public void setText(Component text) {
        this.checkRemoved();
        this.text = text;
        this.entity.setCustomName(text);
    }

    public void remove() {
        this.removed = true;
        this.entity.remove();
    }

    public boolean isRemoved() {
        if(this.removed) return true;
        if(this.entity.getEntity().isRemoved()) {
            remove();
            return true;
        }
        return false;
    }

    public FollowingEntity getEntity() {
        return this.entity;
    }

    public boolean addViewer(@NotNull Player player) {
        return this.entity.addViewer(player);
    }

    public boolean removeViewer(@NotNull Player player) {
        return this.entity.removeViewer(player);
    }

    @NotNull
    public Set<Player> getViewers() {
        return this.entity.getViewers();
    }

    private void checkRemoved() {
        Check.stateCondition(this.isRemoved(), "You cannot interact with a removed Hologram");
    }
}
