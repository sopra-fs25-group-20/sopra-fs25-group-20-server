package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.VotingSession;
import ch.uzh.ifi.hase.soprafs25.exceptions.VoteAlreadyInProgressException;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs25.session.VotingSessionManager;
import org.junit.jupiter.api.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


public class VotingServiceTest {

    private VotingService votingService;
    private GameService gameService;
    private SimpMessagingTemplate messagingTemplate;
    private GameTimerService gameTimerService;

    @BeforeEach
    public void setup() {
        messagingTemplate = mock(SimpMessagingTemplate.class);
        gameService = mock(GameService.class);
        gameTimerService = mock(GameTimerService.class);
        votingService = new VotingService(messagingTemplate, gameService, gameTimerService);

        if (VotingSessionManager.isActive("ROOM123")) {
            VotingSessionManager.removeVotingSession("ROOM123");
        }
    }

    @Test
    public void testCreateVotingSession_success() {
        votingService.createVotingSession("ROOM123", "testUser", "testUser2");
        assertTrue(VotingSessionManager.isActive("ROOM123"));
    }

    @Test
    public void testCreateVotingSession_alreadyActive() {
        votingService.createVotingSession("ROOM123", "testUser", "testUser2");
        assertThrows(VoteAlreadyInProgressException.class, () -> {
            votingService.createVotingSession("ROOM123", "testUser", "testUser2");
        });
    }
}
