package dev.zenqrt.game.ending;

import dev.zenqrt.game.Game;
import dev.zenqrt.game.GamePlayer;
import net.kyori.adventure.title.Title;

import java.util.Collection;

public interface Ending<G extends Game> {

    void execute(G game);

}
