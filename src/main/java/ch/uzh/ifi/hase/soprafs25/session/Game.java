package ch.uzh.ifi.hase.soprafs25.session;

import java.util.Map;
import java.util.HashMap;

public class Game {
    
    private final String roomCode;
    private GamePhase phase;
    private int highlightedImage;
    private final Map<String, String> roles = new HashMap<>();

    public Game(String roomCode) {
        this.roomCode = roomCode;
        this.phase = GamePhase.WAITING;
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

    public void assignRole(String nickname, String role) {
        roles.put(nickname, role);
    }

    public String getRole(String nickname) {
        return roles.get(nickname);
    }
}
