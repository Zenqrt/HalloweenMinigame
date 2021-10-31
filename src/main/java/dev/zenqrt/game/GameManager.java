package dev.zenqrt.game;

import dev.zenqrt.game.halloween.HalloweenGame;
import dev.zenqrt.game.halloween.maze.themes.MazeTheme;
import dev.zenqrt.utils.Utils;
import dev.zenqrt.world.generator.EmptyGenerator;
import dev.zenqrt.world.worlds.HalloweenLobbyWorld;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;
import net.minestom.server.world.biomes.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GameManager {

    private static final DimensionType GAME_DIMENSION = DimensionType.builder(NamespaceID.from("halloween:game_world"))
            .ambientLight(0.05f)
            .fixedTime(18000L)
            .build();

    static {
        MinecraftServer.getDimensionTypeManager().addDimension(GAME_DIMENSION);
    }

    private final Map<Player, GamePlayer> gamePlayers;
    private final Set<Game> games;
    private Game mainGame;

    public GameManager() {
        this.gamePlayers = new HashMap<>();
        this.games = new HashSet<>();

        setMainGame(createHalloweenGame());
    }

    public HalloweenGame createHalloweenGame() {
        var instanceContainer = MinecraftServer.getInstanceManager().createInstanceContainer(GAME_DIMENSION);
        var halloweenGame = new HalloweenGame(ThreadLocalRandom.current().nextInt(10000), instanceContainer);
        halloweenGame.init();

        var theme = halloweenGame.getTheme();
        var name = NamespaceID.from("halloween:" + theme.getClass().getSimpleName());
        var biome = MinecraftServer.getBiomeManager().getByName(name);

        if(biome == null) {
            biome = Biome.builder()
                    .name(name)
                    .effects(halloweenGame.getTheme().getBiomeEffects())
                    .build();
            MinecraftServer.getBiomeManager().addBiome(biome);
        }

        var finalBiome = biome;
        instanceContainer.setChunkGenerator(new EmptyGenerator() {
            @Override
            public void fillBiomes(@NotNull Biome[] biomes, int i, int i1) {
                Arrays.fill(biomes, finalBiome);
            }
        });
        return halloweenGame;
    }

    public Game getGame(int id) {
        return games.stream().filter(game -> game.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void setMainGame(Game mainGame) {
        this.mainGame = mainGame;
    }

    public Game getMainGame() {
        return mainGame;
    }

    public void registerGame(Game game) {
        game.init();
        games.add(game);
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        Utils.putOrReplace(gamePlayers, gamePlayer.getPlayer(), gamePlayer);
    }

    public void removeGamePlayer(Player player) {
        Utils.runIfExisting(gamePlayers.get(player), gamePlayer -> {
            Utils.runIfExisting(gamePlayer.getCurrentGame(), game -> game.leave(player));
            gamePlayers.remove(player);
        });
    }

    public GamePlayer findGamePlayer(Player player) {
        return gamePlayers.get(player);
    }

}
