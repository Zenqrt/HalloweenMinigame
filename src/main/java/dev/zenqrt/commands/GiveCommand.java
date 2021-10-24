package dev.zenqrt.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.zenqrt.item.powerup.InkEggItem;
import net.minestom.server.entity.Player;

@CommandAlias("give")
public class GiveCommand extends BaseCommand {

    @Default
    public void give(Player player) {
        player.getInventory().addItemStack(new InkEggItem().create());
        player.sendMessage("give");
    }
}
