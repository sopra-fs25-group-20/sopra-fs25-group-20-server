package ch.uzh.ifi.hase.soprafs25.entity;

import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;

public class GameResult {

    private int spyGuessIndex;
    private String votedNickname;
    private PlayerRole winnerRole;

    public GameResult(int spyGuessIndex, String votedNickname, PlayerRole winnerRole) {
        this.spyGuessIndex = spyGuessIndex;
        this.votedNickname = votedNickname;
        this.winnerRole = winnerRole;
    }

    public int getSpyGuessIndex() {
        return spyGuessIndex;
    }

    public void setSpyGuessIndex(int spyGuessIndex) {
        this.spyGuessIndex = spyGuessIndex;
    }

    public String getVotedNickname() {
        return votedNickname;
    }

    public void setVotedNickname(String votedNickname) {
        this.votedNickname = votedNickname;
    }

    public PlayerRole getWinnerRole() {
        return winnerRole;
    }

    public void setWinnerRole(PlayerRole winnerRole) {
        this.winnerRole = winnerRole;
    }
}
