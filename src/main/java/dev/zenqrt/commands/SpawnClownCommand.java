package dev.zenqrt.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.zenqrt.entity.monster.KillerClown;
import dev.zenqrt.game.halloween.HalloweenGame;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.fakeplayer.FakePlayerOption;

import java.util.Objects;
import java.util.UUID;

@CommandAlias("spawnclown")
public class SpawnClownCommand extends BaseCommand {

    @Default
    public void spawn(Player player) {
        new KillerClown(new FakePlayerOption(), fakePlayer -> {
            fakePlayer.teleport(player.getPosition());
            fakePlayer.setTeam(HalloweenGame.PLAYER_TEAM);
        });
        player.sendMessage("spawned clown");
    }

}
