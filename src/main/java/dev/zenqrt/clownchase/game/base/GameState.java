package dev.zenqrt.clownchase.game.base;

public abstract class GameState {

    private boolean active;

    protected void onStateStart() {}
    protected void onStateEnd() {}

    public final boolean start() {
        if (active)
            return false;

        active = true;

        onStateStart();
        return true;
    }

    public final boolean end() {
        if (!active) {
            return false;
        }

        active = false;

        onStateEnd();
        return true;
    }

    public boolean canPlayerJoin() {
        return false;
    }
}
