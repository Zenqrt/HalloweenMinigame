package dev.zenqrt.utils.entity;

import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.EntityMeta;
import net.minestom.server.potion.PotionEffect;

import java.util.function.Consumer;

public class EntityUtils {

    public static <T extends EntityMeta> void editMeta(T meta, Consumer<T> function) {
        meta.setNotifyAboutChanges(false);
        function.accept(meta);
        meta.setNotifyAboutChanges(true);
    }

    public static boolean hasActiveEffect(LivingEntity player, PotionEffect potionEffect) {
        for(var effect : player.getActiveEffects()) {
            if(effect.getPotion().getEffect().namespace().equals(potionEffect.namespace())) return true;
        }

        return false;
    }

}
