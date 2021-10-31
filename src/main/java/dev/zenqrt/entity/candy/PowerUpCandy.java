package dev.zenqrt.entity.candy;

import dev.zenqrt.item.CustomItem;
import dev.zenqrt.item.powerup.*;
import dev.zenqrt.utils.chat.ChatUtils;
import dev.zenqrt.utils.chat.ParsedColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minestom.server.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PowerUpCandy extends Candy {

    public static final List<CustomItem> POWER_UPS = new ArrayList<>(Arrays.asList(new StunBallItem(), new InkEggItem(), new SlownessTrapItem(), new DamageTrapItem()));
    public static final List<CustomItem> SPECIAL_POWER_UPS = new ArrayList<>(Arrays.asList(new CandyMagnetItem()));

    private final CustomItem item;

    public PowerUpCandy(CustomItem item) {
        super(Color.PURPLE);

        this.item = item;
    }

    @Override
    public void consume(Player player) {
        super.consume(player);

        var itemStack = item.create();
        player.getInventory().addItemStack(itemStack);

        var displayName = itemStack.getDisplayName();
        if(displayName != null) {
            var color = ParsedColor.of(ChatUtils.TEXT_COLOR_HEX);
            player.sendMessage(MiniMessage.get().parse(color + "You have received <yellow>" + PlainTextComponentSerializer.plainText().serialize(displayName) + color + " item!"));
        }
    }

}
