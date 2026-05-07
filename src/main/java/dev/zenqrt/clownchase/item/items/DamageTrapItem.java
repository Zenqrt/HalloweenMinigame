package dev.zenqrt.clownchase.item.items;

import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.GamePlayerData;
import dev.zenqrt.clownchase.game.states.ChaseGameState;
import dev.zenqrt.clownchase.item.CustomItem;
import dev.zenqrt.clownchase.item.CustomItems;
import dev.zenqrt.clownchase.utils.text.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.GenericGameEvent;
import org.bukkit.inventory.ItemRarity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class DamageTrapItem extends CustomItem {
    private static final String HIT_SHIELD_SOURCE = "game.item.damage_trap.hit_shield.trapper";
    private static final String HIT_NO_SHIELD_SOURCE = "game.item.damage_trap.hit_no_shield.trapper";
    private static final String HIT_SHIELD_VICTIM = "game.item.damage_trap.hit_shield.victim";
    private static final String HIT_NO_SHIELD_VICTIM = "game.item.damage_trap.hit_no_shield.victim";
    private static final String HIT_SHIELD_SELF = "game.item.damage_trap.hit_shield.self";
    private static final String HIT_NO_SHIELD_SELF = "game.item.damage_trap.hit_no_shield.self";
    private static final int CANDY_STEAL = 5;

    public DamageTrapItem() {
        super("damage_trap", Material.CRIMSON_PRESSURE_PLATE, "Damage Trap", ItemRarity.COMMON);
    }

    public static class Listeners extends CustomItem.Listeners {

        private final Map<Location, Player> trapLocations = new HashMap<>();
        private final ChaseGameState state;

        public Listeners(ClownChaseGame game, ChaseGameState state) {
            super(game);

            this.state = state;
        }

        @EventHandler
        public void onBlockPlace(BlockPlaceEvent event) {
            Player player = event.getPlayer();

            if (!isItemUsedInGame(CustomItems.DAMAGE_TRAP, event.getItemInHand(), super.game, player.getUniqueId()))
                return;

            trapLocations.put(event.getBlockPlaced().getLocation(), player);
        }

        @EventHandler
        public void onBlockReceiveGameEvent(GenericGameEvent event) {
            if (event.getEvent() != GameEvent.BLOCK_ACTIVATE)
                return;

            if (!(event.getEntity() instanceof Player player))
                return;


            UUID playerUuid = player.getUniqueId();

            if (!super.game.hasPlayer(playerUuid))
                return;

            Player trapper = trapLocations.remove(event.getLocation());

            if (trapper == null)
                return;

            event.getLocation().getBlock().setBlockData(BlockType.AIR.createBlockData());

            GamePlayerData trapperPlayerData = super.game.getPlayerData(trapper.getUniqueId());

            if (trapper.getUniqueId().equals(playerUuid)) {
                if (trapperPlayerData.isShielded()) {
                    super.game.breakShield(trapper, trapperPlayerData);
                    trapper.sendMessage(Component.translatable(HIT_SHIELD_SELF, NamedTextColor.RED));

                    return;
                }

                trapperPlayerData.removeCandyCollected(CANDY_STEAL);
                this.state.updateCandyLeaderboard();

                trapper.sendMessage(Component.translatable(HIT_NO_SHIELD_SELF, NamedTextColor.RED, Messages.candyText(CANDY_STEAL)));
                return;
            }

            GamePlayerData playerData = super.game.getPlayerData(player.getUniqueId());

            if (playerData.isShielded()) {
                super.game.breakShield(player, playerData);

                trapper.sendMessage(Component.translatable(HIT_SHIELD_SOURCE, NamedTextColor.YELLOW, Messages.username(player)));
                player.sendMessage(Component.translatable(HIT_SHIELD_VICTIM, NamedTextColor.RED, Messages.username(trapper)));

                return;
            }

            playerData.removeCandyCollected(CANDY_STEAL);
            trapperPlayerData.addCandyCollected(CANDY_STEAL);
            this.state.updateCandyLeaderboard();

            trapper.sendMessage(Component.translatable(HIT_NO_SHIELD_SOURCE, NamedTextColor.YELLOW, Messages.username(player), Messages.candyText(CANDY_STEAL)));
            player.sendMessage(Component.translatable(HIT_NO_SHIELD_VICTIM, NamedTextColor.RED, Messages.username(trapper), Messages.candyText(CANDY_STEAL)));
        }
    }

}
