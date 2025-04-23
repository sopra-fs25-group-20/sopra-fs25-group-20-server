package ch.uzh.ifi.hase.soprafs25.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import ch.uzh.ifi.hase.soprafs25.model.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs25.model.ImageIndexDTO;
import ch.uzh.ifi.hase.soprafs25.service.GameService;
import ch.uzh.ifi.hase.soprafs25.service.PlayerConnectionService;

class GameControllerTest {

    private GameService gameService;
    private PlayerConnectionService playerConnectionService;
    private GameController controller;

    @BeforeEach
    void setUp() {
        gameService = mock(GameService.class);
        playerConnectionService = mock(PlayerConnectionService.class);
        controller = new GameController(gameService, playerConnectionService);
    }

    /**
     * Helper to build a Message with session attributes for code and nickname.
     */
    private Message<?> buildMessageWithSession(String code, String nickname) {
        Map<String, Object> sessionAttributes = Map.of(
                "code", code,
                "nickname", nickname
        );
        return MessageBuilder.withPayload(new byte[0])
                .setHeader("simpSessionAttributes", sessionAttributes)
                .build();
    }

    @Test
    void startGame_shouldCallStartRound() {
        Message<?> msg = buildMessageWithSession("room123", "Alice");
        controller.startGame(msg);
        verify(gameService).startRound("room123", "Alice");
    }

    @Test
    void spyGuess_shouldCallHandleSpyGuess() {
        ImageIndexDTO dto = new ImageIndexDTO();
        dto.setIndex(3);
        Message<?> msg = buildMessageWithSession("room123", "Bob");
        controller.spyGuess(dto, msg);
        verify(gameService).handleSpyGuess("room123", "Bob", 3);
    }

    @Test
    void gameSettings_shouldCallChangeGameSettings() {
        GameSettingsDTO settings = new GameSettingsDTO(10, 20, 2, "US");
        Message<?> msg = buildMessageWithSession("room123", "Carol");
        controller.gameSettings(settings, msg);
        verify(gameService).changeGameSettings("room123", "Carol", settings);
    }

    @Test
    void kickPlayer_shouldCallKickPlayer() {
        Message<?> msg = buildMessageWithSession("roomA", "Admin");
        controller.kickPlayer("Dave", msg);
        verify(playerConnectionService).kickPlayer("Admin", "Dave", "roomA");
    }

    @Test
    void getPlayerRole_shouldCallBroadcastPersonalizedRole() {
        Message<?> msg = buildMessageWithSession("roomX", "Eve");
        controller.getPlayerRole(msg);
        verify(gameService).broadcastPersonalizedRole("roomX", "Eve");
    }

    @Test
    void getImageIndex_shouldCallBroadcastPersonalizedImageIndex() {
        Message<?> msg = buildMessageWithSession("roomY", "Frank");
        controller.getImageIndex(msg);
        verify(gameService).broadcastPersonalizedImageIndex("roomY", "Frank");
    }
}