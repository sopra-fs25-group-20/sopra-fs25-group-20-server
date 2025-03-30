package ch.uzh.ifi.hase.soprafs25.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "VotingSession")
public class VotingSession implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long voteSessionId;

    @Column(nullable = false)
    private String roomCode;

    @Column(nullable = false)
    private String initiator;

    @Column(nullable = false)
    private String target;

    @Column(nullable = false)
    private boolean isActive = true;

    @ElementCollection
    @CollectionTable(name = "VotingSessionVotes", joinColumns = @JoinColumn(name = "voteSessionId"))
    @MapKeyColumn(name = "voter")
    @Column(name = "voteChoice")
    private Map<String, Boolean> votes = new HashMap<>();

    public VotingSession() {}

    public VotingSession(String roomCode, String initiator, String target) {
        this.roomCode = roomCode;
        this.initiator = initiator;
        this.target = target;
    }

    public void castVote(String voter, boolean voteYes) {
        votes.putIfAbsent(voter, voteYes);
    }

    public Map<String, Boolean> getVotes() {
        return votes;
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