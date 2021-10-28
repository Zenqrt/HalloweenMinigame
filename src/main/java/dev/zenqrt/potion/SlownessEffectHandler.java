package dev.zenqrt.potion;

import net.minestom.server.attribute.Attribute;
import net.minestom.server.attribute.AttributeModifier;
import net.minestom.server.attribute.AttributeOperation;
import net.minestom.server.entity.Player;

public class SlownessEffectHandler extends PotionEffectHandler {

    private AttributeModifier attributeModifier;

    public SlownessEffectHandler(Player player) {
        super(player);
    }

    @Override
    public void onApply(Context context) {
        var player = context.getPlayer();
        var potion = context.getPotion();
        var movementSpeed = player.getAttribute(Attribute.MOVEMENT_SPEED);
        var value = -((movementSpeed.getValue() * 0.15F) * potion.getAmplifier());

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
    public void onRemove(Context context) {
        var player = context.getPlayer();
        player.getAttribute(Attribute.MOVEMENT_SPEED).removeModifier(attributeModifier);
    }
}
