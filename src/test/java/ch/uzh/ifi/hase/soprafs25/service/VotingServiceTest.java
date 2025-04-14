package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.VotingSession;
import ch.uzh.ifi.hase.soprafs25.exceptions.VoteAlreadyInProgressException;
import ch.uzh.ifi.hase.soprafs25.session.VotingSessionManager;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


class VotingServiceTest {
    
    private VotingService votingService;

    @BeforeEach
    void setup() {
        votingService = new VotingService();
        if (VotingSessionManager.isActive("ROOM123")) {
            VotingSessionManager.removeVotingSession("ROOM123");
        }
    }

    @Test
    void testCreateVotingSession_success() {
        VotingSession session = votingService.createVotingSession("ROOM123", "testUser", "testUser2");

        assertNotNull(session);
        assertEquals("testUser", session.getInitiator());
        assertEquals("testUser2", session.getTarget());
    }

    @Test
    void testCreateVotingSession_alreadyActive() {
        votingService.createVotingSession("ROOM123", "testUser", "testUser2");
        assertThrows(VoteAlreadyInProgressException.class, () -> {
            votingService.createVotingSession("ROOM123", "testUser", "testUser2");
        });
    }

    @Test
    void testCastVote_andCompletion() {
        votingService.createVotingSession("ROOM123", "testUser", "testUser2");
        boolean cast1 = votingService.castVote("ROOM123", "testUser3", true);
        boolean cast2 = votingService.castVote("ROOM123", "testUser3", false);
        assertTrue(cast1);
        assertFalse(cast2);
        assertTrue(votingService.isVoteComplete("ROOM123", 1));
    }
}
