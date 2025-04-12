package ch.uzh.ifi.hase.soprafs25.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.*;

public class VotingSessionTest {
    
    private VotingSession votingSession;

    @BeforeEach
    public void setup() {
        votingSession = new VotingSession("ROOM123", "testUser", "testUser2");
    }

    @Test
    public void testVotingSessionInitialization() {
        assertEquals("ROOM123", votingSession.getRoomCode());
        assertEquals("testUser", votingSession.getInitiator());
        assertEquals("testUser2", votingSession.getTarget());
        assertTrue(votingSession.isActive());
        assertNotNull(votingSession.getVoteState());
    }

    @Test
    public void testCastVoteAndHasVoted() {
        assertFalse(votingSession.hasVoted("testUser"));
        votingSession.castVote("testUser", true);
        assertTrue(votingSession.hasVoted("testUser"));
        assertEquals(true, votingSession.getVoteState().getVotes().get("testUser"));
    }

    @Test
    public void testDoubleVotingNotAllowed() {
        votingSession.castVote("testUser2", true);
        votingSession.castVote("testUser2", false);
        assertEquals(1, votingSession.getVoteState().getVotes().size());
        assertTrue(votingSession.getVoteState().getVotes().get("testUser2"));
    }
}
