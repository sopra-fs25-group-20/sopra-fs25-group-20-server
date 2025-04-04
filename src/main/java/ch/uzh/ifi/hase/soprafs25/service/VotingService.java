package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.VotingSession;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VotingService {
    
    private final Map<String, VotingSession> sessions = new ConcurrentHashMap<>();
    

    public void startVote(String roomCode, String initiator, String target) {
        VotingSession session = new VotingSession(roomCode, initiator, target);
        sessions.put(roomCode, session);
    }

    public void castVote(String roomCode, String voter, boolean voteYes) {
        VotingSession session = sessions.get(roomCode);
        if (session != null) {
            session.castVote(voter, voteYes);
        }
    }

    public Map<String, Boolean> getVotes(String roomCode) {
        VotingSession session = sessions.get(roomCode);
        return session != null ? session.getVotes() : Map.of();
    }

    public String getInitiator(String roomCode) {
        VotingSession session = sessions.get(roomCode);
        return session != null ? session.getInitiator() : null;
    }

    public String getTarget(String roomCode) {
        VotingSession session = sessions.get(roomCode);
        return session != null ? session.getTarget() : null;
    }

    public void endVote(String roomCode) {
        sessions.remove(roomCode);
    }

    public boolean hasVoted(String roomCode, String voter) {
        VotingSession session = sessions.get(roomCode);
        return session != null && session.getVotes().containsKey(voter);
    }

    public boolean isVoteSessionActive(String roomCode) {
        return sessions.containsKey(roomCode);
    }
}