package dev.zenqrt.entity.monster;

import dev.zenqrt.entity.player.PlayerEntity;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.fakeplayer.FakePlayer;
import net.minestom.server.entity.fakeplayer.FakePlayerOption;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public class KillerClown extends PlayerEntity {

    public KillerClown(@NotNull FakePlayerOption option, @Nullable Consumer<FakePlayer> spawnCallback) {
        super(UUID.randomUUID(), "murder_clown", option, spawnCallback);
        this.setSkin(new PlayerSkin(
                "ewogICJ0aW1lc3RhbXAiIDogMTYzNTA0MzEwNTc4MSwKICAicHJvZmlsZUlkIiA6ICJiN2JmZmU1MDIzYzI0YTU5OWM0MzkyNmI5MjgxYjFjNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJjaHJpc3RpYW4xNjIiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDVkZWMzNjcyNGJmZDM4Y2U5ZTY3ZGE1ZGZkMDY1ZDViMzU4YTE5ZTcwM2M3YjkzYmZjOGRlYTQ3NjlhMTNhZCIKICAgIH0sCiAgICAiQ0FQRSIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjM0MGMwZTAzZGQyNGExMWIxNWE4YjMzYzJhN2U5ZTMyYWJiMjA1MWIyNDgxZDBiYTdkZWZkNjM1Y2E3YTkzMyIKICAgIH0KICB9Cn0=",
                "KZU6fenp8/GkOiMVaE826LnN94iN7239leDtbxLGPWvysX164NX9XutWOONRg6yiflDvpMToUtZYDuLlxz+X8RngTipQKSIzI0mvSPjpyhnq19Uo6aBHjeuCFsrHz7LVCRHU+vQPUReataDQC918jXfkLGNPgH0fM1QHtF9Rs/4VvFvyMQZe+4JENK/BUBADVClNVfqh3y2phr7/5tt+E9RmFaXxjbe6s6aKx8eAhMD+m3G/k3L1iD9p6WpiwHZHvotDXFhkZz1nS+J62mgLOF2njwstgbAkyGaNYpASVo0oggb00aYfcp4ixQZ5nb1mBV099EhfdOyKdgBn2HIMpj1cvmIwG0m4ebyJ5wsPQl8W8NwsTrmt0He2TNw+20rLqd1GlCnwhUmnt1wmi/Fz2zWHM/3h4TXXT5YptnFwvvqoT3HDX5rXLa9MtIiqx4pIuZycH9nBcbOTvo9rzZIUmlNUAhl8WkZ37r/XifIQhhbHkesHOTWuAkE5M/Ua+vVpBCQ3GE/aSxjaPPUVCyAAbUTyWLC5XUy8ViiccMpi+2RrSHMzYD/JLDkmYnQZzHd5G8LJQO5QhLURyDzrgi+U/IiiaYu7g+WQFEGvWVylzJyBzkrNb03jwLj491oGUQ+pAemdy9mY21YGnGFjP2tr4hhUfxKjR2WGkNwMnQ8jwuM="
        ));
        this.setItemInMainHand(ItemStack.of(Material.IRON_AXE));
    }
}
