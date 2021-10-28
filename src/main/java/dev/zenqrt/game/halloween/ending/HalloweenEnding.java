package dev.zenqrt.game.halloween.ending;

import dev.zenqrt.game.ending.Ending;
import dev.zenqrt.game.halloween.HalloweenGame;

public class HalloweenEnding implements Ending<HalloweenGame> {

    @Override
    public void execute(HalloweenGame game) {
        var sorted = game.getSortedCandiesCollected(3);
        // TODO: get top 3 winners
    }
}
