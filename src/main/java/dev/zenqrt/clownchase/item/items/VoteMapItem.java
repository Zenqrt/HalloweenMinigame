package dev.zenqrt.clownchase.item.items;

import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.item.CustomItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemRarity;

public final class VoteMapItem extends CustomItem {

    public VoteMapItem() {
        super("vote_map", Material.BELL, "Vote Map", ItemRarity.UNCOMMON);
    }

    public static class Listeners extends CustomItem.Listeners {

        public Listeners(ClownChaseGame game) {
            super(game);
        }

        @EventHandler
        public void onRightClick(PlayerInteractEvent event) {
            Player player = event.getPlayer();

            if (!game.hasPlayer(player.getUniqueId()))
                return;


        }
    }

}
