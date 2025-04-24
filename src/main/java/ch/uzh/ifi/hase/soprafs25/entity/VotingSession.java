package ch.uzh.ifi.hase.soprafs25.entity;

import ch.uzh.ifi.hase.soprafs25.session.VoteState;

public class VotingSession {

    private final String roomCode;
    private final String initiator;
    private final String target;
    private boolean isActive = true;
    private final transient VoteState voteState = new VoteState();

    public VotingSession(String roomCode, String initiator, String target) {
        this.roomCode = roomCode;
        this.initiator = initiator;
        this.target = target;
    }

    public void castVote(String voter, boolean voteYes) {
        voteState.addVote(voter, voteYes);
    }

    public boolean hasVoted(String voter) {
        return voteState.hasVoted(voter);
    }

    public VoteState getVoteState() {
        return voteState;
    }

    public String getInitiator() {
        return initiator;
    }

    public String getTarget() {
        return target;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}