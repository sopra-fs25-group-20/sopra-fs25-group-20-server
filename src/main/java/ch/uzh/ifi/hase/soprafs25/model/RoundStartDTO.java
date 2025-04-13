package ch.uzh.ifi.hase.soprafs25.model;

import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;

public class RoundStartDTO {
    private Integer highlightedImageIndex;
    private PlayerRole role;

    public Integer getHighlightedImageIndex() {
        return highlightedImageIndex;
    }

    public void setHighlightedImageIndex(Integer highlightedImageIndex) {
        this.highlightedImageIndex = highlightedImageIndex;
    }

    public PlayerRole getRole() {
        return role;
    }

    public void setRole(PlayerRole role) {
        this.role = role;
    }
}
