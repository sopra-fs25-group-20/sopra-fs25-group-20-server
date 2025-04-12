package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.VotingSession;
import ch.uzh.ifi.hase.soprafs25.exceptions.VoteAlreadyInProgressException;
import ch.uzh.ifi.hase.soprafs25.session.VotingSessionManager;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


public class VotingServiceTest {
    
    private VotingService votingService;

    @BeforeEach
    public void setup() {
        votingService = new VotingService();
        if (VotingSessionManager.isActive("ROOM123")) {
            VotingSessionManager.removeVotingSession("ROOM123");
        }
    }

    @Test
    public void testCreateVotingSession_success() {
        VotingSession session = votingService.createVotingSessionIfNotActive("ROOM123", "testUser", "testUser2");

        assertNotNull(session);
        assertEquals("testUser", session.getInitiator());
        assertEquals("testUser2", session.getTarget());
    }

    @Test
    public void testCreateVotingSession_alreadyActive() {
        votingService.createVotingSessionIfNotActive("ROOM123", "testUser", "testUser2");
        assertThrows(VoteAlreadyInProgressException.class, () -> {
            votingService.createVotingSessionIfNotActive("ROOM123", "testUser", "testUser2");
        });
    }

    @Test
    public void testCastVote_andCompletion() {
        votingService.createVotingSessionIfNotActive("ROOM123", "testUser", "testUser2");
        boolean cast1 = votingService.castVote("ROOM123", "testUser3", true);
        boolean cast2 = votingService.castVote("ROOM123", "testUser3", false);
        assertTrue(cast1);
        assertFalse(cast2);
        assertTrue(votingService.isVoteComplete("ROOM123", 1));
    }
}
