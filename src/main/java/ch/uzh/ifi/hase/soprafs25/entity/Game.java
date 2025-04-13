package ch.uzh.ifi.hase.soprafs25.entity;

import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;
import ch.uzh.ifi.hase.soprafs25.util.RoleUtil;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class Game {
    
    private final String roomCode;
    private GamePhase phase;
    private int highlightedImageIndex;
    private final Map<String, PlayerRole> roles = new HashMap<>();
    private final GameSettings gameSettings;
    private GameResult gameResult;
    private final List<byte[]> images = new ArrayList<>();

    public Game(String roomCode) {
        this.roomCode = roomCode;
        this.phase = GamePhase.LOBBY;
        this.gameSettings = new GameSettings(1, 30, 300, 9, "europe");
    }

    public PlayerRole getRole(String nickname) {
        return roles.get(nickname);
    }

    public GameResult getGameResult() {
        return gameResult;
    }

    public void setGameResult(int spyGuessIndex, String votedNickname, PlayerRole winnerRole) {
        this.gameResult = new GameResult(spyGuessIndex, votedNickname, winnerRole);
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }

    public void setGameSettings(int votingTimer, int gameTimer, String imageRegion, int imageCount) {
        this.gameSettings.setVotingTimer(votingTimer);
        this.gameSettings.setGameTimer(gameTimer);
        this.gameSettings.setImageRegion(imageRegion);
        this.gameSettings.setImageCount(imageCount);
    }

    public Map<String, PlayerRole> getRoles() {
        return roles;
    }

    public void assignRoles(List<String> nicknames) {
        roles.clear();
        roles.putAll(RoleUtil.assignRoles(nicknames, gameSettings.getSpyCount()));
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

    public int getHighlightedImageIndex() {
        return highlightedImageIndex;
    }

    public void setHighlightedImageIndex(int highlightedImageIndex) {
        this.highlightedImageIndex = highlightedImageIndex;
    }

    public List<byte[]> getImages() {
        return images;
    }

    public void setImages(List<byte[]> newImages) {
        images.clear();
        images.addAll(newImages);
    }
}
