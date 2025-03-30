package ch.uzh.ifi.hase.soprafs25.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VotingService {
    
    private final Map<String, Map<String, Boolean>> roomVotes = new ConcurrentHashMap<>();
    private final Map<String, String> voteInitiators = new ConcurrentHashMap<>();
    private final Map<String, String> voteTargets = new ConcurrentHashMap<>();

    public void startVote(String roomCode, String initiator, String target) {
        roomVotes.put(roomCode, new ConcurrentHashMap<>());
        voteInitiators.put(roomCode, initiator);
        voteTargets.put(roomCode, target);
    }

    public void castVote(String roomCode, String voter, boolean voteYes) {
        roomVotes.get(roomCode).put(voter, voteYes);
    }

    public Map<String, Boolean> getVotes(String roomCode) {
        return roomVotes.getOrDefault(roomCode, new ConcurrentHashMap<>());
    }

    public String getInitiator(String roomCode) {
        return voteInitiators.get(roomCode);
    }

    public String getTarget(String roomCode) {
        return voteTargets.get(roomCode);
    }

    public void clearVote(String roomCode) {
        roomVotes.remove(roomCode);
        voteInitiators.remove(roomCode);
        voteTargets.remove(roomCode);
    }

}