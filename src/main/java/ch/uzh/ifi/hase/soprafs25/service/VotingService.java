package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.VotingSession;
import org.springframework.stereotype.Service;
import ch.uzh.ifi.hase.soprafs25.exceptions.VoteAlreadyInProgressException;
import ch.uzh.ifi.hase.soprafs25.session.VotingSessionManager;

@Service
public class VotingService {
    
    public VotingSession createVotingSession(String roomCode, String initiator, String target) {
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

    public boolean castVote(String roomCode, String voter, boolean voteYes) {
        VotingSession session = getActiveVotingSession(roomCode);
        if (session.getVoteState().hasVoted(voter)) {
            return false;
        }
        session.getVoteState().addVote(voter, voteYes);
        return true;
    }

    public boolean isVoteComplete(String roomCode, int expectedVotes) {
        VotingSession session = getActiveVotingSession(roomCode);
        return session.getVoteState().getVotes().size() == expectedVotes;
    }
}
