package dev.zenqrt.clownchase.game;

public final class GamePlayerData {

    private boolean shielded;
    private int candyCollected;
    private boolean alive;

    public GamePlayerData() {
        this.candyCollected = 0;
        this.alive = true;
    }

    public int getCandyCollected() {
        return candyCollected;
    }

    public void addCandyCollected(int candy) {
        candyCollected += candy;
    }

    public void removeCandyCollected(int candy) {
        candyCollected -= candy;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setShielded(boolean shielded) {
        this.shielded = shielded;
    }

    public boolean isShielded() {
        return shielded;
    }
}
