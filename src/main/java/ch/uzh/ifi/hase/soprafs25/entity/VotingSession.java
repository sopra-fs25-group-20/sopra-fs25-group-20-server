package ch.uzh.ifi.hase.soprafs25.entity;

import ch.uzh.ifi.hase.soprafs25.model.VoteState;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "VotingSession")
public class VotingSession implements Serializable {
    
    @Id
    @GeneratedValue
    private Long voteSessionId;

    @Column(nullable = false)
    private String roomCode;

    @Column(nullable = false)
    private String initiator;

    @Column(nullable = false)
    private String target;

    @Column(nullable = false)
    private boolean isActive = true;

    @Transient // in memory only
    private final VoteState voteState = new VoteState();

    public VotingSession() {}

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

    public Long getVoteSessionId() {
        return voteSessionId;
    }
}