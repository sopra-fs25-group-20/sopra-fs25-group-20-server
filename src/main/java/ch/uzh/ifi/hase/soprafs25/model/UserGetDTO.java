package ch.uzh.ifi.hase.soprafs25.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserGetDTO {

    private String username;
    private int wins;
    private int defeats;
    private int games;

    @JsonProperty("current_streak")
    private int currentStreak;
    @JsonProperty("highest_streak")
    private int highestStreak;

    public UserGetDTO(String username, int wins, int defeats, int games, int currentStreak, int highestStreak) {
        this.username = username;
        this.wins = wins;
        this.defeats = defeats;
        this.games = games;
        this.currentStreak = currentStreak;
        this.highestStreak = highestStreak;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getDefeats() {
        return defeats;
    }

    public void setDefeats(int defeats) {
        this.defeats = defeats;
    }

    public int getGames() {
        return games;
    }

    public void setGames(int games) {
        this.games = games;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getHighestStreak() {
        return highestStreak;
    }

    public void setHighestStreak(int highestStreak) {
        this.highestStreak = highestStreak;
    }
}