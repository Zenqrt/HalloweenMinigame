package dev.zenqrt.timer.countdown;

import java.util.function.Consumer;

public class CountdownRunnable implements Runnable {

    private final Consumer<Integer> function;

    protected int timer;

    public CountdownRunnable(int time, Consumer<Integer> function) {
        this.function = function;
        this.timer = time;
    }

    @Override
    public final void run() {
        function.accept(timer);
        timer--;
    }
}
