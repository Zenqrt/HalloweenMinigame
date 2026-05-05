package dev.zenqrt.clownchase.game.states;

import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.ClownChasePlayer;
import dev.zenqrt.clownchase.game.base.GameState;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.Position;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public final class SetupPlayersGameState extends GameState {

    private static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(NamespacedKey.minecraft("game_speed"), 0.5, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
    private static final double MAX_HEALTH = 6;
    private final ClownChaseGame game;

    public SetupPlayersGameState(ClownChaseGame game) {
        this.game = game;
    }

    @Override
    protected void onStateStart() {
        // FIXME: This should work but it doesn't because of the weird way the maze build was generated.
        // FIXME: To fix this, I have to redo MazeBuilder but I can't be bothered right now so I'll just use the legacy code for finding valid spawns
//        List<BlockPosition> openCells = new ArrayList<>();
//
//        for (int y = 0; y < this.game.getBoard().getDimensionY(); y++){
//            for (int x = 0; x < this.game.getBoard().getDimensionX(); x++) {
//                if (this.game.getBoard().getBlock(x, y) == 0)
//                    openCells.add(Position.block(x*6, 42, y*6));
//            }
//        }
//
//        for (ClownChasePlayer gamePlayer : game.getPlayers().values()) {
//            BlockPosition position = openCells.get(ThreadLocalRandom.current().nextInt(openCells.size()));
//            Player player = gamePlayer.validatePlayer();
//
//            player.teleport(position.offset(0.5, 0, 0.5).toLocation(game.getGameWorld()), PlayerTeleportEvent.TeleportCause.PLUGIN);
//        }

        // ----- LEGACY IMPL -----
        for (ClownChasePlayer gamePlayer : game.getPlayers().values()) {
            int xMax = this.game.getBoard().getDimensionX() * 6; // 6 is the scale. This should not be hardcoded like this TODO <--------
            int yMax = this.game.getBoard().getDimensionY() * 6;

            ThreadLocalRandom random = ThreadLocalRandom.current();
            BlockPosition position;

            do {
                position = Position.block(random.nextInt(xMax), 42, random.nextInt(yMax));
            } while (!isSurroundingAreaOpen(this.game.getGameWorld(), position, 1, 1));

            Player player = gamePlayer.validatePlayer();

            // Set player properties
            player.teleport(position.toLocation(this.game.getGameWorld()));
            player.setGameMode(GameMode.ADVENTURE);
            player.setFoodLevel(20);
            player.setHealth(MAX_HEALTH);
            player.setExp(0);
            player.setLevel(0);

            AttributeInstance movementSpeed = Objects.requireNonNull(player.getAttribute(Attribute.MOVEMENT_SPEED), "movementSpeed");
            movementSpeed.addTransientModifier(SPEED_MODIFIER);

            AttributeInstance maxHealth = Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH), "maxHealth");
            AttributeModifier healthModifier = new AttributeModifier(NamespacedKey.minecraft("game_health"), MAX_HEALTH - maxHealth.getValue(), AttributeModifier.Operation.ADD_NUMBER);
            maxHealth.addTransientModifier(healthModifier);
        }

        this.game.nextState();
    }

    private static boolean isSurroundingAreaOpen(World world, BlockPosition origin, int xArea, int zArea) {
        for (int x = -xArea; x <= xArea; x++) {
            for (int z = -zArea; z <= zArea; z++) {
                BlockPosition position = origin.offset(x, 0, z);

                if (!world.getBlockAt(position.blockX(), position.blockY(), position.blockZ()).isEmpty())
                    return false;
            }
        }
        return true;
    }
}
