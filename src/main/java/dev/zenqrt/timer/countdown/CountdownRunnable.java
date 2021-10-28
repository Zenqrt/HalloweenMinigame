package dev.zenqrt.timer.countdown;

import dev.zenqrt.timer.MinestomRunnable;
import org.jetbrains.annotations.ApiStatus;

public class CountdownRunnable extends MinestomRunnable {

    protected int timer;

    public CountdownRunnable(int time) {
        this.timer = time;
    }

    @ApiStatus.OverrideOnly
    public void beforeIncrement() {
    }

    @ApiStatus.OverrideOnly
    public void afterIncrement() {
    }

    @ApiStatus.OverrideOnly
    public void endCountdown() {
    }

    @Override
    public void run() {
        if(timer <= 0) {
            endCountdown();
            this.cancel();
            return;
        }
        beforeIncrement();
        timer--;
        afterIncrement();
    }
}
