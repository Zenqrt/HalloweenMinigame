package dev.zenqrt.utils.entity;

import net.minestom.server.entity.metadata.EntityMeta;

import java.util.function.Consumer;

public class EntityUtils {

    public static <T extends EntityMeta> void editMeta(T meta, Consumer<T> function) {
        meta.setNotifyAboutChanges(false);
        function.accept(meta);
        meta.setNotifyAboutChanges(true);
    }

}
