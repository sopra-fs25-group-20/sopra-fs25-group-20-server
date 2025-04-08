package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.VotingSession;
import org.springframework.stereotype.Service;
import ch.uzh.ifi.hase.soprafs25.exceptions.VoteAlreadyInProgressException;
import ch.uzh.ifi.hase.soprafs25.session.VotingSessionManager;

@Service
public class VotingService {
    
    public VotingSession createVotingSessionIfNotActive(String roomCode, String initiator, String target) {
        if (VotingSessionManager.isActive(roomCode)) {
            throw new VoteAlreadyInProgressException(roomCode);
        }

        VotingSession session = new VotingSession(roomCode, initiator, target);
        VotingSessionManager.addVotingSession(session);
        return session;
    }

    public VotingSession getActiveVotingSession(String roomCode) {
        return VotingSessionManager.getVotingSession(roomCode);
    }

    public void endVotingSession(String roomCode) {
        VotingSessionManager.removeVotingSession(roomCode);
    }
}
