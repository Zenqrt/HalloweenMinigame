package dev.zenqrt.game.ending;

import dev.zenqrt.game.GamePlayer;
import net.kyori.adventure.title.Title;

import java.util.Collection;

public interface Ending {

    Title getTitle();
    void execute(Collection<GamePlayer> gamePlayers);

}
