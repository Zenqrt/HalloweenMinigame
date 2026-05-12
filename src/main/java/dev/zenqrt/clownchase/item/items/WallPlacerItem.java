package dev.zenqrt.clownchase.item.items;

import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.item.CustomItem;
import dev.zenqrt.clownchase.item.CustomItems;
import dev.zenqrt.clownchase.utils.world.PositionUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemRarity;

import java.util.HashMap;
import java.util.Map;

public final class WallPlacerItem extends CustomItem {

    private static final int WALL_X_RADIUS = 1;
    private static final int WALL_HEIGHT = 3;
    private static final int WALL_PERSIST_TIME = 200;  // 10 seconds
    private static final Sound PLACE_SOUND = Sound.sound(Key.key("minecraft:block.anvil.land"), Sound.Source.MASTER, 1, 2);

    public WallPlacerItem() {
        super("wall_placer", Material.BRICKS, "Wall Placer", ItemRarity.COMMON);
    }

    public static class Listeners extends CustomItem.Listeners {

        public Listeners(ClownChaseGame game) {
            super(game);
        }

        @EventHandler
        public void onBlockPlace(BlockPlaceEvent event) {
            final Player player = event.getPlayer();

            if (!isItemUsedInGame(CustomItems.WALL_PLACER, event.getItemInHand(), super.game, player.getUniqueId()))
                return;

            BlockData brickData = BlockType.BRICKS.createBlockData();
            Map<Block, BlockData> previousBlocks = new HashMap<>();

            Block blockPlaced = event.getBlockPlaced();
            BlockFace tangentFace = PositionUtils.fromYaw(player.getYaw() + 90);

            previousBlocks.put(blockPlaced, BlockType.AIR.createBlockData());

            for (int x = -WALL_X_RADIUS; x <= WALL_X_RADIUS; x++) {
                for (int y = 0; y < WALL_HEIGHT; y++) {
                    Block block = blockPlaced
                            .getRelative(tangentFace, x)
                            .getRelative(BlockFace.UP, y);

                    if (block.getType() == Material.BRICKS)
                        continue;

                    previousBlocks.put(block, block.getBlockData());
                    block.setBlockData(brickData);
                }
            }

            player.getWorld().playSound(PLACE_SOUND, blockPlaced.getX(), blockPlaced.getY(), blockPlaced.getZ());

            Bukkit.getScheduler().runTaskLater(super.game.getPlugin(), () -> {
                previousBlocks.forEach(Block::setBlockData);
                previousBlocks.clear();
            }, WALL_PERSIST_TIME);
        }
    }

}
