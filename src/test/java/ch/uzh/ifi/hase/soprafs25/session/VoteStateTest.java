package ch.uzh.ifi.hase.soprafs25.session;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.util.Map;

public class VoteStateTest {
    
    private VoteState voteState;

    @BeforeEach
    public void setup() {
        voteState = new VoteState();
    }

    @Test
    public void testAddVoteOnceOnly() {
        voteState.addVote("testUser", true);
        voteState.addVote("testUser", false);

        Map<String, Boolean> votes = voteState.getVotes();
        assertEquals(1, votes.size());
        assertTrue(votes.get("testUser"));
    }

    @Test
    public void testCountYesAndNoVotes() {
        voteState.addVote("testUser", true);
        voteState.addVote("testUser2", false);
        voteState.addVote("testUser3", true);

        assertEquals(2, voteState.countYesVotes());
        assertEquals(1, voteState.countNoVotes());
    }

    @Test
    public void testHasVoted() {
        assertFalse(voteState.hasVoted("testUser"));
        voteState.addVote("testUser", true);
        assertTrue(voteState.hasVoted("testUser"));
    }

    @Test
    public void testVotesUnmodifiableFromOutside() {
        voteState.addVote("testUser", true);
        Map<String, Boolean> externalMap = voteState.getVotes();
        assertThrows(UnsupportedOperationException.class, () -> {
            externalMap.put("testUser2", false);
        });
    }
}
