package dev.zenqrt.timer;

import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskBuilder;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public abstract class MinestomRunnable implements Runnable {
    private Duration delayDuration = Duration.ZERO;
    private Duration repeatDuration = Duration.ZERO;

    @Nullable
    private Task currentTask;

    public MinestomRunnable delay(Duration duration) {
        this.delayDuration = duration;
        return this;
    }

    public MinestomRunnable repeat(Duration duration) {
        this.repeatDuration = duration;
        return this;
    }

    public Task schedule() {
        Duration delay = delayDuration != Duration.ZERO ? delayDuration : Duration.ZERO;
        Duration repeat = repeatDuration != Duration.ZERO ? repeatDuration : Duration.ZERO;

        TaskBuilder taskBuilder = MinecraftServer.getSchedulerManager().buildTask(this)
                .delay(delay)
                .repeat(repeat);

        return currentTask = taskBuilder.schedule();
    }

    public void cancel() {
        if (currentTask == null) return;
        currentTask.cancel();
    }
}
