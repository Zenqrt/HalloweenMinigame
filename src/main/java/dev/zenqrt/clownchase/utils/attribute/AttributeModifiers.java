package dev.zenqrt.clownchase.utils.attribute;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;

public final class AttributeModifiers {

    private static final NamespacedKey FREEZE_KEY = NamespacedKey.minecraft("freeze");

    private AttributeModifiers() {}

    public static AttributeModifier freeze(AttributeInstance movementSpeed) {

        return new AttributeModifier(FREEZE_KEY, -movementSpeed.getValue(), AttributeModifier.Operation.ADD_NUMBER);
    }

}
