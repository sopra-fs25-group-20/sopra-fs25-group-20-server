package ch.uzh.ifi.hase.soprafs25.model;

public class VoteStateDTO {

    private int numberVotesTrue;
    private int numberVotesFalse;

    public VoteStateDTO(int numberVotesTrue, int numberVotesFalse) {
        this.numberVotesTrue = numberVotesTrue;
        this.numberVotesFalse = numberVotesFalse;
    }

    public int getNumberVotesTrue() {
        return numberVotesTrue;
    }

    public void setNumberVotesTrue(int numberVotesTrue) {
        this.numberVotesTrue = numberVotesTrue;
    }

    public int getNumberVotesFalse() {
        return numberVotesFalse;
    }

    public void setNumberVotesFalse(int numberVotesFalse) {
        this.numberVotesFalse = numberVotesFalse;
    }
}