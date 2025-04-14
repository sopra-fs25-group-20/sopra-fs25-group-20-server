package ch.uzh.ifi.hase.soprafs25.session;

import ch.uzh.ifi.hase.soprafs25.entity.VotingSession;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class VotingSessionManagerTest {

    private static final String ROOM_CODE = "ROOM123";

    @BeforeEach
    void cleanup() {
        if (VotingSessionManager.isActive(ROOM_CODE)) {
            VotingSessionManager.removeVotingSession(ROOM_CODE);
        }
    }

    @Test
    void testAddAndGetVotingSession() {
        VotingSession session = new VotingSession(ROOM_CODE, "testUser", "testUser2");
        VotingSessionManager.addVotingSession(session);

        VotingSession retrieved = VotingSessionManager.getVotingSession(ROOM_CODE);
        assertNotNull(retrieved);
        assertEquals("testUser", retrieved.getInitiator());
        assertEquals("testUser2", retrieved.getTarget());
    }

    @Test
    void testRemoveVotingSession() {
        VotingSession session = new VotingSession(ROOM_CODE, "testUser", "testUser2");
        VotingSessionManager.addVotingSession(session);
        VotingSessionManager.removeVotingSession(ROOM_CODE);

        assertFalse(VotingSessionManager.isActive(ROOM_CODE));
    }

    @Test
    void testGetVotingSession_NotFound() {
        assertThrows(IllegalStateException.class, () -> {
            VotingSessionManager.getVotingSession("NON_EXISTENT_ROOM");
        });
    }

    @Test
    public void testRemoveVotingSession_NotFound() {
        assertThrows(IllegalStateException.class, () -> {
            VotingSessionManager.removeVotingSession("NON_EXISTENT_ROOM");
        });
    }
}
