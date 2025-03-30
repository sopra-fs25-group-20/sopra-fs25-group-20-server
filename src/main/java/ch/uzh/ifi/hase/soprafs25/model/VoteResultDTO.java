package ch.uzh.ifi.hase.soprafs25.model;

import java.util.Map;


public class VoteResultDTO {
    
    private Map<String, Boolean> votes;
    private String initiator;
    private String target;

    public Map<String, Boolean> getVotes() {
        return votes;
    }

    public void setVotes(Map<String, Boolean> votes) {
        this.votes = votes;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}