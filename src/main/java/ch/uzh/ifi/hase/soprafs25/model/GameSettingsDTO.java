package ch.uzh.ifi.hase.soprafs25.model;

public class GameSettingsDTO {
    private int votingTimer;
    private int gameTimer;
    private int imageCount;
    private String imageRegion;

    public int getVotingTimer() {
        return votingTimer;
    }

    public int getGameTimer() {
        return gameTimer;
    }

    public int getImageCount() {
        return imageCount;
    }

    public String getImageRegion() {
        return imageRegion;
    }
}
