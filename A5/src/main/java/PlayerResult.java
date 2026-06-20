public class PlayerResult {
    private String playerName;
    private int finalScore;

    public PlayerResult() {
    }

    public PlayerResult(String playerName, int finalScore) {
        this.playerName = playerName;
        this.finalScore = finalScore;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getFinalScore() {
        return finalScore;
    }
}

