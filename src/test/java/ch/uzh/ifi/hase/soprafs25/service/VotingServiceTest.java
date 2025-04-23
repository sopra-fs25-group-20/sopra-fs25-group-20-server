package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;
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

    @Test
    void fullVotingSession_playersVoteSpyOut_completesVotingAndEvaluatesResult() {
        when(gameReadService.getPlayerCount("ROOM123")).thenReturn(3);
        Game game = GameSessionManager.getGameSession("ROOM123");
        game.getRoles().clear();
        game.getRoles().put("player0", PlayerRole.INNOCENT);
        game.getRoles().put("player1", PlayerRole.INNOCENT);
        game.getRoles().put("player2", PlayerRole.SPY);

        votingService.initializeVotingSession("ROOM123", "player0", "player2");

        reset(gameService, gameBroadcastService);

        votingService.castVote("ROOM123", "player0", true);
        votingService.castVote("ROOM123", "player1", true);
        votingService.castVote("ROOM123", "player2", false);

        assertFalse(VotingSessionManager.isActive("ROOM123"));

        assertEquals(PlayerRole.INNOCENT, game.getGameResult().getWinnerRole());
        assertEquals("player2", game.getGameResult().getVotedNickname());

        then(gameService).should().advancePhase("ROOM123", GamePhase.SUMMARY);
    }
}
