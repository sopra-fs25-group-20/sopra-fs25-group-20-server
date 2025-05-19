package ch.uzh.ifi.hase.soprafs25.model;

import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;
import ch.uzh.ifi.hase.soprafs25.entity.GameResult;

import java.util.Map;

public class GameResultDTO {

    private final Map<String, PlayerRole> roles;
    private final Map<String, String> colors;
    private final int highlightedImageIndex;
    private final Integer spyGuessIndex;
    private final String votedNickname;
    private final PlayerRole winnerRole;

    public GameResultDTO(Map<String, PlayerRole> roles,
                         Map<String, String> colors,
                         int highlightedImageIndex,
                         GameResult gameResult) {
        this.roles = roles;
        this.colors = colors;
        this.highlightedImageIndex = highlightedImageIndex;
        this.spyGuessIndex = gameResult.getSpyGuessIndex();
        this.votedNickname = gameResult.getVotedNickname();
        this.winnerRole = gameResult.getWinnerRole();
    }

    public Map<String, PlayerRole> getRoles() {
        return roles;
    }

    public Map<String, String> getColors() {
        return colors;
    }
    
    public int getHighlightedImageIndex() {
        return highlightedImageIndex;
    }

    public Integer getSpyGuessIndex() {
        return spyGuessIndex;
    }

    public String getVotedNickname() {
        return votedNickname;
    }

    public PlayerRole getWinnerRole() {
        return winnerRole;
    }
}
