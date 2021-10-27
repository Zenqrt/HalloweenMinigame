package dev.zenqrt.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import dev.zenqrt.entity.monster.KillerClown;
import dev.zenqrt.game.halloween.HalloweenGame;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.fakeplayer.FakePlayerOption;

@CommandAlias("spawnclown")
@CommandPermission("events.command.spawnclown")
public class SpawnClownCommand extends BaseCommand {

    @Default
    public void spawn(Player player) {
        var clown = new KillerClown(new FakePlayerOption(), fakePlayer -> {
            fakePlayer.teleport(player.getPosition());
            fakePlayer.setTeam(HalloweenGame.PLAYER_TEAM);
        });
        clown.setTarget(player);
        clown.setAttacking(true);
        player.sendMessage("spawned clown");
    }

}
