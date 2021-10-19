package dev.zenqrt.entity;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;

public class RotatingArmorStand extends LivingEntity {

    public RotatingArmorStand() {
        super(EntityType.ARMOR_STAND);
    }

    @Override
    public void tick(long time) {
        super.tick(time);

        var meta = (ArmorStandMeta) this.getLivingEntityMeta();
        meta.setHeadRotation(new Vec(0, meta.getHeadRotation().y() + 5, 0));
    }
}
