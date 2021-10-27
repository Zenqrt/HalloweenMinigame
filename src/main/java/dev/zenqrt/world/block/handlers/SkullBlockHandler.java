package dev.zenqrt.world.block.handlers;

import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public class SkullBlockHandler implements BlockHandler {

    @Override
    public @NotNull Collection<Tag<?>> getBlockEntityTags() {
        return Arrays.asList(Tag.String("ExtraType"), Tag.NBT("SkullOwner"));
    }

    @Override
    public byte getBlockEntityAction() {
        return (byte) 4;
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from("minecraft:skull");
    }
}
