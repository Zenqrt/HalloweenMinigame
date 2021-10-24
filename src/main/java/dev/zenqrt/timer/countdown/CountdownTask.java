package dev.zenqrt.timer.countdown;

import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CountdownTask extends Task {

    private final CountdownRunnable runnable;

    public CountdownTask(@NotNull SchedulerManager schedulerManager, @NotNull CountdownRunnable runnable, boolean shutdown, long delay, long repeat, boolean isTransient, @Nullable String owningExtension) {
        super(schedulerManager, runnable, shutdown, delay, repeat, isTransient, owningExtension);
        this.runnable = runnable;
    }

    @Override
    public final void run() {
        super.run();

        if(runnable.timer < 0) {
            this.cancel();
        }
    }
}
