package ch.uzh.ifi.hase.soprafs25.model;

import java.util.Map;

public class VoteStateDTO {
    
    private Map<String, Boolean> votes;

    public VoteStateDTO(Map<String, Boolean> votes) {
        this.votes = votes;
    }

    public Map<String, Boolean> getVotes() {
        return votes;
    }

    public void setVotes(Map<String, Boolean> votes) {
        this.votes = votes;
    }
}
