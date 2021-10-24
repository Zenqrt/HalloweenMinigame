package dev.zenqrt.timer;

import net.minestom.server.extras.selfmodification.MinestomRootClassLoader;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

public abstract class GenericTaskBuilder<T extends Task, R extends Runnable> {

    protected final SchedulerManager schedulerManager;
    protected final R runnable;
    protected final boolean shutdown;
    protected final String owningExtension;
    protected long delay;
    protected long repeat;
    protected boolean isTransient;

    public GenericTaskBuilder(@NotNull SchedulerManager schedulerManager, @NotNull R runnable) {
        this(schedulerManager, runnable, false);
    }

    public GenericTaskBuilder(@NotNull SchedulerManager schedulerManager, @NotNull R runnable, boolean shutdown) {
        this.schedulerManager = schedulerManager;
        this.runnable = runnable;
        this.shutdown = shutdown;
        this.isTransient = false;
        this.owningExtension = MinestomRootClassLoader.findExtensionObjectOwner(runnable);
    }

    @NotNull
    public GenericTaskBuilder<T, R> delay(long time, @NotNull TemporalUnit unit) {
        return this.delay(Duration.of(time, unit));
    }

    @NotNull
    public GenericTaskBuilder<T, R> delay(@NotNull Duration duration) {
        this.delay = duration.toMillis();
        return this;
    }

    @NotNull
    public GenericTaskBuilder<T, R> repeat(long time, @NotNull TemporalUnit unit) {
        return this.repeat(Duration.of(time, unit));
    }

    @NotNull
    public GenericTaskBuilder<T, R> repeat(@NotNull Duration duration) {
        this.repeat = duration.toMillis();
        return this;
    }

    @NotNull
    public GenericTaskBuilder<T, R> clearDelay() {
        this.delay = 0L;
        return this;
    }

    @NotNull
    public GenericTaskBuilder<T, R> clearRepeat() {
        this.repeat = 0L;
        return this;
    }

    public GenericTaskBuilder<T, R> makeTransient() {
        this.isTransient = true;
        return this;
    }

    public abstract T build();

    @NotNull
    public T schedule() {
        T task = this.build();
        task.schedule();
        return task;
    }

}
