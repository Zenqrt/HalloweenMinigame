package dev.zenqrt.potion;

import net.minestom.server.attribute.Attribute;
import net.minestom.server.attribute.AttributeModifier;
import net.minestom.server.attribute.AttributeOperation;
import net.minestom.server.entity.Player;
import net.minestom.server.potion.Potion;

public class SlownessEffectHandler extends PotionEffectHandler {

    private AttributeModifier attributeModifier;

    public SlownessEffectHandler(Player player) {
        super(player);
    }

    @Override
    public void onApply(Potion potion) {
        var movementSpeed = player.getAttribute(Attribute.MOVEMENT_SPEED);
        var value = -((movementSpeed.getBaseValue() * 0.15F) * potion.getAmplifier());

        for(var modifier : movementSpeed.getModifiers()) {
            if(modifier.getName().equals("slowness") && modifier.getAmount() <= value) {
                this.attributeModifier = modifier;
                return;
            }
        }

        this.attributeModifier = new AttributeModifier("slowness", value, AttributeOperation.ADDITION);
        movementSpeed.addModifier(attributeModifier);
    }

    @Override
    public void onRemove(Potion potion) {
        player.getAttribute(Attribute.MOVEMENT_SPEED).removeModifier(attributeModifier);
    }
}
