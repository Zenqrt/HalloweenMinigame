package dev.zenqrt.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.zenqrt.item.powerup.CandyMagnetItem;
import dev.zenqrt.item.powerup.InkEggItem;
import dev.zenqrt.item.powerup.SlownessTrapItem;
import dev.zenqrt.item.powerup.StunBallItem;
import net.minestom.server.entity.Metadata;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.PlayerMeta;
import net.minestom.server.network.packet.server.play.EntityEffectPacket;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;

import java.util.ArrayList;

@CommandAlias("give")
public class GiveCommand extends BaseCommand {

    @Default
    public void give(Player player) {
//        player.getInventory().addItemStack(new InkEggItem().create());
//        player.getInventory().addItemStack(new StunBallItem().create());
//        player.getInventory().addItemStack(new SlownessTrapItem().create());
//        player.getInventory().addItemStack(new CandyMagnetItem().create());
//        player.sendMessage("give");
////
//        PlayerMeta meta = player.getEntityMeta();
//        meta.setNotifyAboutChanges(false);
//        meta.setHasGlowingEffect(true);
//        var packet = player.getMetadataPacket();
//        player.getPlayerConnection().sendPacket(packet);
//        meta.setHasGlowingEffect(false);
    }
}
