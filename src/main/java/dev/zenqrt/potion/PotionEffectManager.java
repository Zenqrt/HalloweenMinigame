package dev.zenqrt.potion;

import dev.zenqrt.timer.MinestomRunnable;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.potion.Potion;
import net.minestom.server.timer.Task;
import net.minestom.server.utils.time.TimeUnit;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/* Known Issues
* Smaller amplified effects will replace larger amplified effects
 */
public class PotionEffectManager {

    private final Map<Player, Task> effects = new HashMap<>();

    public void applyEffect(Player player, Potion potion, PotionEffectHandler handler) {
        var context = new PotionEffectHandler.Context(player, potion);
        player.addEffect(potion);
        handler.onApply(context);

        Task task;
        if(handler.isTickable()) {
            task = new MinestomRunnable() {
                int timer = potion.getDuration();
                @Override
                public void run() {
                    if(timer <= 0) {
                        handler.onRemove(context);
                        this.cancel();
                        return;
                    }
                    handler.tick(context);
                    timer--;
                }
            }.repeat(Duration.of(potion.getDuration(), TimeUnit.SERVER_TICK)).schedule();
        } else {
            task = MinecraftServer.getSchedulerManager().buildTask(() -> handler.onRemove(context))
                    .delay(Duration.of(potion.getDuration(), TimeUnit.SERVER_TICK)).schedule();
        }

        if(effects.containsKey(player)) {
            effects.get(player).cancel();
            effects.replace(player, task);
        } else {
            effects.put(player, task);
        }
    }

}
