package dev.zenqrt.entity.monster;

import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.fakeplayer.FakePlayer;
import net.minestom.server.entity.fakeplayer.FakePlayerOption;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public class MurderClown extends FakePlayer {

    protected MurderClown(@NotNull UUID uuid, @NotNull String username, @NotNull FakePlayerOption option, @Nullable Consumer<FakePlayer> spawnCallback) {
        super(uuid, username, option, spawnCallback);
    }

    @Override
    public void spawn() {
        super.spawn();

        this.setUsernameField("murder_clown");
        this.setSkin(new PlayerSkin(
                "ewogICJ0aW1lc3RhbXAiIDogMTYyNzQyMDcxOTE1NiwKICAicHJvZmlsZUlkIiA6ICJjNDlkMzdkMjBlNzQ0MDYxYjJiNDk2MzliODlmZjU0OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJCZW1wdGF5IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2U2NDc2N2NmMmZlYzAyYmI1MTc1YjE5OTFmOTkzYWIzMzY4NDA2Nzc3YTYyNmNkMzcyOTM2MWQ2ODg1ODFkZjIiCiAgICB9CiAgfQp9",
                "KZU6fenp8/GkOiMVaE826LnN94iN7239leDtbxLGPWvysX164NX9XutWOONRg6yiflDvpMToUtZYDuLlxz+X8RngTipQKSIzI0mvSPjpyhnq19Uo6aBHjeuCFsrHz7LVCRHU+vQPUReataDQC918jXfkLGNPgH0fM1QHtF9Rs/4VvFvyMQZe+4JENK/BUBADVClNVfqh3y2phr7/5tt+E9RmFaXxjbe6s6aKx8eAhMD+m3G/k3L1iD9p6WpiwHZHvotDXFhkZz1nS+J62mgLOF2njwstgbAkyGaNYpASVo0oggb00aYfcp4ixQZ5nb1mBV099EhfdOyKdgBn2HIMpj1cvmIwG0m4ebyJ5wsPQl8W8NwsTrmt0He2TNw+20rLqd1GlCnwhUmnt1wmi/Fz2zWHM/3h4TXXT5YptnFwvvqoT3HDX5rXLa9MtIiqx4pIuZycH9nBcbOTvo9rzZIUmlNUAhl8WkZ37r/XifIQhhbHkesHOTWuAkE5M/Ua+vVpBCQ3GE/aSxjaPPUVCyAAbUTyWLC5XUy8ViiccMpi+2RrSHMzYD/JLDkmYnQZzHd5G8LJQO5QhLURyDzrgi+U/IiiaYu7g+WQFEGvWVylzJyBzkrNb03jwLj491oGUQ+pAemdy9mY21YGnGFjP2tr4hhUfxKjR2WGkNwMnQ8jwuM="
        ));
        this.setItemInMainHand(ItemStack.of(Material.IRON_AXE));
    }

    @Override
    public void tick(long time) {
        super.tick(time);
        if(isDead) return;
    }
}
