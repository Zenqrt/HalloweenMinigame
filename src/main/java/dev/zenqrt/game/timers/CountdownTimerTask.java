package dev.zenqrt.game.timers;

import dev.zenqrt.function.Procedure;
import dev.zenqrt.timer.countdown.CountdownRunnable;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class CountdownTimerTask extends CountdownRunnable {

    public CountdownTimerTask(int time, Audience audience, String messageFormat, Procedure endingFunction) {
        super(time, timer -> {
            if(timer == 0) {
                endingFunction.run();
                return;
            }
            if(timer % 10 == 0 || timer <= 5) {
                audience.sendActionBar(MiniMessage.get().parse(String.format(messageFormat, timer)));
            }
        });
    }
}
