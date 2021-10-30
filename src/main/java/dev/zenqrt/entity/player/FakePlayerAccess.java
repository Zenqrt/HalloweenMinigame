package dev.zenqrt.entity.player;

import com.extollit.gaming.ai.path.HydrazinePathFinder;
import com.extollit.gaming.ai.path.SchedulingPriority;
import net.minestom.server.entity.Metadata;
import net.minestom.server.entity.fakeplayer.FakePlayer;
import net.minestom.server.entity.fakeplayer.FakePlayerOption;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public class FakePlayerAccess extends FakePlayer {

    public FakePlayerAccess(@NotNull UUID uuid, @NotNull String username, @NotNull FakePlayerOption option, @Nullable Consumer<FakePlayer> spawnCallback) {
        super(uuid, username, option, spawnCallback);
    }

    @Override
    public void spawn() {
        var navigator = this.getNavigator();
        var hydrazinePathFinder = new HydrazinePathFinder(navigator.getPathingEntity(), this.getInstance().getInstanceSpace());
        hydrazinePathFinder.schedulingPriority(SchedulingPriority.extreme);
        navigator.setPathFinder(hydrazinePathFinder);

    }

    public void enableSkinLayers() {
        this.metadata.setIndex(17, Metadata.Byte((byte) 0x40));
    }
}
