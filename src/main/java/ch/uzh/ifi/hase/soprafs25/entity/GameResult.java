package ch.uzh.ifi.hase.soprafs25.entity;

import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;

public class GameResult {

    private Integer spyGuessIndex;
    private String votedNickname;
    private PlayerRole winnerRole;

    public GameResult(Integer spyGuessIndex, String votedNickname, PlayerRole winnerRole) {
        this.spyGuessIndex = spyGuessIndex;
        this.votedNickname = votedNickname;
        this.winnerRole = winnerRole;
    }

    public Integer getSpyGuessIndex() {
        return spyGuessIndex;
    }

    public void setSpyGuessIndex(Integer spyGuessIndex) {
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
