package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.VotingSession;
import ch.uzh.ifi.hase.soprafs25.exceptions.VoteAlreadyInProgressException;
import ch.uzh.ifi.hase.soprafs25.session.VotingSessionManager;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class VotingServiceTest {

    private VotingService votingService;
    private GameService gameService;
    private GameBroadcastService gameBroadcastService;
    private GameReadService gameReadService;

    @BeforeEach
    void setup() {
        gameService = mock(GameService.class);
        gameBroadcastService = mock(GameBroadcastService.class);
        gameReadService = mock(GameReadService.class);

        votingService = new VotingService(gameService, gameBroadcastService, gameReadService);

        if (VotingSessionManager.isActive("ROOM123")) {
            VotingSessionManager.removeVotingSession("ROOM123");
        }
    }

    @Test
    void testInitializeVotingSession_success() {
        votingService.initializeVotingSession("ROOM123", "testUser", "targetUser");

        VotingSession session = VotingSessionManager.getVotingSession("ROOM123");

        assertNotNull(session);
        assertEquals("testUser", session.getInitiator());
        assertEquals("targetUser", session.getTarget());

        verify(gameService).advancePhase(eq("ROOM123"), any());
        verify(gameBroadcastService).broadcastVoteStart("ROOM123", "targetUser");
        verify(gameBroadcastService).broadcastVoteState("ROOM123");
    }

    @Test
    void testInitializeVotingSession_alreadyActive_throwsException() {
        votingService.initializeVotingSession("ROOM123", "A", "B");

        assertThrows(VoteAlreadyInProgressException.class, () -> {
            votingService.initializeVotingSession("ROOM123", "C", "D");
        });
    }

    @Test
    void testVotingCompletesAndSessionEnds() {
        when(gameReadService.getPlayerCount("ROOM123")).thenReturn(2);

        votingService.initializeVotingSession("ROOM123", "initiator", "target");

        votingService.castVote("ROOM123", "voter1", true);
        votingService.castVote("ROOM123", "voter2", false);

        assertFalse(VotingSessionManager.isActive("ROOM123"));

        verify(gameService, atLeastOnce()).advancePhase(eq("ROOM123"), any());
    }
}
