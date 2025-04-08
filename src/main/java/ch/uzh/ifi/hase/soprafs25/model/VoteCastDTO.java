package ch.uzh.ifi.hase.soprafs25.model;


public class VoteCastDTO {

    private String voter;
    private boolean voteYes;

    public String getVoter() {
        return voter;
    }

    public void setVoter(String voter) {
        this.voter = voter;
    }

    public boolean isVoteYes() {
        return voteYes;
    }

    public void setVoteYes(boolean voteYes) {
        this.voteYes = voteYes;
    }
}