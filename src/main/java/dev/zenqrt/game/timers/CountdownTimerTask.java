package dev.zenqrt.game.timers;

import dev.zenqrt.function.Procedure;
import dev.zenqrt.timer.countdown.CountdownRunnable;

import java.util.function.Consumer;

public class CountdownTimerTask extends CountdownRunnable {

    private final Consumer<Integer> timerFunction;
    private final Procedure endingFunction;

    public CountdownTimerTask(int time, Consumer<Integer> timerFunction, Procedure endingFunction) {
        super(time);

        this.timerFunction = timerFunction;
        this.endingFunction = endingFunction;
    }

    @Override
    public void beforeIncrement() {
        if(timer % 10 == 0 || timer <= 5) {
            timerFunction.accept(timer);
        }
    }

    @Override
    public void endCountdown() {
        endingFunction.run();
    }
}
