package ch.uzh.ifi.hase.soprafs25.session;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

public class VoteState {
    
    private final Map<String, Boolean> votes = new HashMap<>();

    public void addVote(String voter, boolean voteYes){
        votes.putIfAbsent(voter, voteYes);
    }

    public boolean hasVoted(String voter) {
        return votes.containsKey(voter);
    }

    public int countYesVotes() {
        return (int) votes.values().stream().filter(Boolean::booleanValue).count();
    }

    public int countNoVotes() {
        return (int) votes.values().stream().filter(v -> !v).count();
    }

    public Map<String, Boolean> getVotes() {
        return Collections.unmodifiableMap(votes);
    }
}