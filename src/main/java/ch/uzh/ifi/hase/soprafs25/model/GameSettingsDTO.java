package ch.uzh.ifi.hase.soprafs25.model;

public class GameSettingsDTO {
    private final int votingTimer;
    private final int gameTimer;
    private final int imageCount;
    private final String imageRegion;

    public GameSettingsDTO(int votingTimer, int gameTimer, int imageCount, String imageRegion) {
        this.votingTimer = votingTimer;
        this.gameTimer = gameTimer;
        this.imageCount = imageCount;
        this.imageRegion = imageRegion;
    }

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
