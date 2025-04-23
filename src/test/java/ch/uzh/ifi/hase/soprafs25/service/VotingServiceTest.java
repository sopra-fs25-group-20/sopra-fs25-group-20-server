package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs25.entity.Game;
import ch.uzh.ifi.hase.soprafs25.entity.VotingSession;
import ch.uzh.ifi.hase.soprafs25.exceptions.VoteAlreadyInProgressException;
import ch.uzh.ifi.hase.soprafs25.session.GameSessionManager;
import ch.uzh.ifi.hase.soprafs25.session.VotingSessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.then;
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

        if (!GameSessionManager.isActive("ROOM123")) {
            GameSessionManager.addGameSession(new Game("ROOM123"));
        }
    }

    @Test
    void testInitializeVotingSession_success() {
        votingService.initializeVotingSession("ROOM123", "testUser", "targetUser");

        VotingSession session = VotingSessionManager.getVotingSession("ROOM123");

        assertNotNull(session);
        assertEquals("testUser", session.getInitiator());
        assertEquals("targetUser", session.getTarget());

        then(gameService).should().advancePhase("ROOM123", GamePhase.VOTE);
        then(gameBroadcastService).should().broadcastVoteStart("ROOM123", "targetUser");
        then(gameBroadcastService).should().broadcastVoteState("ROOM123", 0, 0);
    }

    @Test
    void testInitializeVotingSession_alreadyActive_throwsException() {
        votingService.initializeVotingSession("ROOM123", "A", "B");
        assertThrows(VoteAlreadyInProgressException.class,
                () -> votingService.initializeVotingSession("ROOM123", "C", "D"));
    }

    @Test
    void testVoteStateBroadcast() {
        when(gameReadService.getPlayerCount("ROOM123")).thenReturn(2);
        votingService.initializeVotingSession("ROOM123", "init", "targ");

        reset(gameBroadcastService);

        votingService.castVote("ROOM123", "voter1", true);

        then(gameBroadcastService).should().broadcastVoteState("ROOM123", 1, 0);
        assertTrue(VotingSessionManager.isActive("ROOM123"));
    }
}
