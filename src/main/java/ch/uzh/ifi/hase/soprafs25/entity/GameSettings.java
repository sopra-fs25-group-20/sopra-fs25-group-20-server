package ch.uzh.ifi.hase.soprafs25.entity;

public class GameSettings {

    private final int spyCount;
    private int votingTimer;
    private int gameTimer;
    private int imageCount;
    private String imageRegion;

    public GameSettings(int spyCount, int votingTimer, int gameTimer, int imageCount, String imageRegion) {
        this.spyCount = spyCount;
        this.votingTimer = votingTimer;
        this.gameTimer = gameTimer;
        this.imageCount = imageCount;
        this.imageRegion = imageRegion;
    }

    public int getSpyCount() {
        return spyCount;
    }

    public int getVotingTimer() {
        return votingTimer;
    }

    public void setVotingTimer(int votingTimer) {
        this.votingTimer = votingTimer;
    }

    public int getGameTimer() {
        return gameTimer;
    }

    public void setGameTimer(int gameTimer) {
        this.gameTimer = gameTimer;
    }

    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }

    public String getImageRegion() {
        return imageRegion;
    }

    public void setImageRegion(String imageRegion) {
        this.imageRegion = imageRegion;
    }
}
