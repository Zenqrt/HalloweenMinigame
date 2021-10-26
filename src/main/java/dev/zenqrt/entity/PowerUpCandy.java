package dev.zenqrt.entity;

import dev.zenqrt.item.CustomItem;
import net.minestom.server.entity.Player;

public class PowerUpCandy extends Candy {

    private final CustomItem item;

    public PowerUpCandy(CustomItem item) {
        super(Color.PURPLE);

        this.item = item;
    }

    @Override
    public void consume(Player player) {
        super.consume(player);

        player.getInventory().addItemStack(item.create());
    }
}
