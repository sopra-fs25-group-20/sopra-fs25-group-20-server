package ch.uzh.ifi.hase.soprafs25.session;

import ch.uzh.ifi.hase.soprafs25.util.RoleUtil;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class Game {
    
    private final String roomCode;
    private GamePhase phase;
    private int highlightedImage;
    private final Map<String, String> roles = new HashMap<>();
    private final int spyCount;

    public Game(String roomCode) {
        this.roomCode = roomCode;
        this.phase = GamePhase.WAITING;
        this.spyCount = 1; // default for now
    }

    public String getRoomCode() {
        return roomCode;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    public int getHighlightedImage() {
        return highlightedImage;
    }

    public void setHighlightedImage(int highlightedImage) {
        this.highlightedImage = highlightedImage;
    }

    public Map<String, String> getRoles() {
        return roles;
    }

    public void assignRoles(List<String> nicknames) {
        roles.clear();
        roles.putAll(RoleUtil.assignRoles(nicknames, spyCount));
    }

    public String getRole(String nickname) {
        return roles.get(nickname);
    }
}
