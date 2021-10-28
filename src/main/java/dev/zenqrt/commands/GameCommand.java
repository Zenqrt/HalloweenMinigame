package dev.zenqrt.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import dev.zenqrt.game.Game;
import dev.zenqrt.game.GameManager;
import dev.zenqrt.game.GamePlayer;
import dev.zenqrt.game.halloween.HalloweenGame;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

@CommandAlias("game")
@Description("Game command.")
public class GameCommand extends BaseCommand {

    private final GameManager gameManager;

    public GameCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Subcommand("create")
    public void createGame(Player player) {
        player.sendMessage("Creating game...");
        var game = new HalloweenGame(ThreadLocalRandom.current().nextInt(10000), MinecraftServer.getInstanceManager().createInstanceContainer());
        gameManager.registerGame(game);
        player.sendMessage("Created game (id = " + game.getId() + ")!");
    }

    @Subcommand("join")
    public void joinGame(Player player, int id) {
        var game = gameManager.getGame(id);
        if(game == null) {
            player.sendMessage("A game with that id does not exist!");
            return;
        }

        player.sendMessage("Joining...");
        game.join(gameManager.findGamePlayer(player));
    }

    @Subcommand("start")
    public void startGame(GamePlayer gamePlayer, @Default("10") int seconds) {
        var currentGame = gamePlayer.getCurrentGame();
        if(currentGame == null) throw new InvalidCommandArgument("You are not in a game!");
        gamePlayer.getPlayer().sendMessage("Starting game countdown...");
        currentGame.startCountdown(seconds);
    }

}
