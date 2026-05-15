package dev.zenqrt.clownchase.game.states;

import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.ClownChasePlayer;
import dev.zenqrt.clownchase.game.base.GameState;
import dev.zenqrt.clownchase.utils.attribute.GameAttributeModifiers;
import io.papermc.paper.math.BlockPosition;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.Objects;

public final class SetupPlayersGameState extends GameState {

    private static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(GameAttributeModifiers.SPEED_KEY, 0.5, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
    private final ClownChaseGame game;

    public SetupPlayersGameState(ClownChaseGame game) {
        this.game = game;
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    protected void onStateStart() {
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
            player.getInventory().clear();

            AttributeInstance movementSpeed = Objects.requireNonNull(player.getAttribute(Attribute.MOVEMENT_SPEED), "movementSpeed");
            movementSpeed.addTransientModifier(SPEED_MODIFIER);

            AttributeInstance maxHealth = Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH), "maxHealth");
            AttributeModifier healthModifier = new AttributeModifier(
                    GameAttributeModifiers.MAX_HEALTH_KEY,
                    this.game.getGameSettings().maxHealth() - maxHealth.getValue(),
                    AttributeModifier.Operation.ADD_NUMBER
            );
            maxHealth.addTransientModifier(healthModifier);
        }

        this.game.nextState();
    }
}
