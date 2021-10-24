package dev.zenqrt.timer.countdown;

import dev.zenqrt.timer.GenericTaskBuilder;
import net.minestom.server.timer.SchedulerManager;
import org.jetbrains.annotations.NotNull;

public class CountdownTaskBuilder extends GenericTaskBuilder<CountdownTask, CountdownRunnable> {

    public CountdownTaskBuilder(@NotNull SchedulerManager schedulerManager, @NotNull CountdownRunnable runnable) {
        super(schedulerManager, runnable);
    }

    @Override
    public CountdownTask build() {
        return new CountdownTask(this.schedulerManager, this.runnable, this.shutdown, this.delay, this.repeat, this.isTransient, this.owningExtension);
    }
}
