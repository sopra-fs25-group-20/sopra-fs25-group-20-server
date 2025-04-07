package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.VotingSession;
import org.springframework.stereotype.Service;

@Service
public class VotingService {
    
    public VotingSession createVotingSession(String roomCode, String initiator, String target) {
        return new VotingSession(roomCode, initiator, target);
    }
}
