package dev.zenqrt.game;

public class GameOptions {

    public final int minPlayers,maxPlayers;
    public final boolean canJoinInGame;

    private GameOptions(int minPlayers, int maxPlayers, boolean canJoinInGame) {
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.canJoinInGame = canJoinInGame;
    }

    public static class Builder {

        private int maxPlayers,minPlayers;
        private boolean canJoinInGame;

        public Builder maxPlayers(int maxPlayers) {
            this.maxPlayers = maxPlayers;
            return this;
        }

        public int maxPlayers() {
            return maxPlayers;
        }

        public Builder minPlayers(int minPlayers) {
            this.minPlayers = minPlayers;
            return this;
        }

        public int minPlayers() {
            return minPlayers;
        }

        public Builder canJoinInGame(boolean canJoinInGame) {
            this.canJoinInGame = canJoinInGame;
            return this;
        }

        public boolean canJoinInGame() {
            return canJoinInGame;
        }

        public GameOptions build() {
            return new GameOptions(minPlayers, maxPlayers, canJoinInGame);
        }
    }

}
