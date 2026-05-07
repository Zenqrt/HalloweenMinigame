package dev.zenqrt.clownchase.game.states;

import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.ClownChasePlayer;
import dev.zenqrt.clownchase.game.base.GameState;
import io.papermc.paper.math.BlockPosition;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.Objects;

public final class SetupPlayersGameState extends GameState {

    private static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(NamespacedKey.minecraft("game_speed"), 0.5, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
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
            Player player = gamePlayer.validatePlayer();
            BlockPosition spawn = this.game.findAvailableSpawn(1, 1);

            // Set player properties
            player.teleport(spawn.toLocation(this.game.getGameWorld()));
            player.setGameMode(GameMode.SURVIVAL);
            player.setFoodLevel(20);
            player.setHealth(this.game.getGameSettings().maxHealth());
            player.setExp(0);
            player.setLevel(0);

            AttributeInstance movementSpeed = Objects.requireNonNull(player.getAttribute(Attribute.MOVEMENT_SPEED), "movementSpeed");
            movementSpeed.addTransientModifier(SPEED_MODIFIER);

            AttributeInstance maxHealth = Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH), "maxHealth");
            AttributeModifier healthModifier = new AttributeModifier(
                    NamespacedKey.minecraft("game_health"),
                    this.game.getGameSettings().maxHealth() - maxHealth.getValue(),
                    AttributeModifier.Operation.ADD_NUMBER
            );
            maxHealth.addTransientModifier(healthModifier);
        }

        this.game.nextState();
    }
}
