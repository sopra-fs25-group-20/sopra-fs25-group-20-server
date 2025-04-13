package ch.uzh.ifi.hase.soprafs25.model;

import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;

import java.util.Map;

public class ResultDTO {
    private Map<String, PlayerRole> roles;
    private PlayerRole winnerRole;
    private int highlightedImageIndex;

    public ResultDTO() {}

    public ResultDTO(Map<String, PlayerRole> roles, PlayerRole winnerRole, int highlightedImageIndex) {
        this.roles = roles;
        this.winnerRole = winnerRole;
        this.highlightedImageIndex = highlightedImageIndex;
    }

    public Map<String, PlayerRole> getRoles() {
        return roles;
    }

    public int getHighlightedImageIndex() {
        return highlightedImageIndex;
    }

    public PlayerRole getWinnerRole() {
        return winnerRole;
    }
}
