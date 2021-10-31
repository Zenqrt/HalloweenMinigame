package dev.zenqrt.potion;

import net.minestom.server.attribute.Attribute;
import net.minestom.server.attribute.AttributeModifier;
import net.minestom.server.attribute.AttributeOperation;
import net.minestom.server.entity.Player;
import net.minestom.server.potion.Potion;

public class SpeedEffectHandler extends PotionEffectHandler {

    private AttributeModifier attributeModifier;

    public SpeedEffectHandler(Player player) {
        super(player);
    }

    @Override
    public void onApply(Potion potion) {
        var movementSpeed = player.getAttribute(Attribute.MOVEMENT_SPEED);
        var value = ((movementSpeed.getBaseValue() * 0.2F) * potion.getAmplifier());

        for(var modifier : movementSpeed.getModifiers()) {
            if(modifier.getName().equals("speed") && modifier.getAmount() <= value) {
                this.attributeModifier = modifier;
                return;
            }
        }

        this.attributeModifier = new AttributeModifier("speed", value, AttributeOperation.ADDITION);
        movementSpeed.addModifier(attributeModifier);
    }

    @Override
    public void onRemove(Potion potion) {
        player.getAttribute(Attribute.MOVEMENT_SPEED).removeModifier(attributeModifier);
    }
}
