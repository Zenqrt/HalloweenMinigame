package dev.zenqrt.clownchase.utils.entity;

import dev.zenqrt.clownchase.utils.reflection.ReflectionUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;

public final class EntityAccessor {

    public static final EntityDataAccessor<Byte> DATA_SHARED_FLAGS_ID = ReflectionUtils.getStaticDeclaredField(Entity.class, "DATA_SHARED_FLAGS_ID");

    private EntityAccessor() {}

}
