package ch.uzh.ifi.hase.soprafs25.websocket;

import ch.uzh.ifi.hase.soprafs25.service.GameBroadcastService;
import ch.uzh.ifi.hase.soprafs25.service.PlayerConnectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSocketDisconnectListenerTest {

    @Mock
    private PlayerConnectionService playerConnectionService;

    @Mock
    private GameBroadcastService gameBroadcastService;

    private WebSocketDisconnectListener listener;

    @BeforeEach
    void setUp() {
        listener = new WebSocketDisconnectListener(playerConnectionService, gameBroadcastService);
    }

    @Test
    void handleWebSocketDisconnect_withSessionAttributes_callsServices() {
        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put("nickname", "user1");
        sessionAttributes.put("code",     "ROOM42");
        sessionAttributes.put("color",    "red");

        Message<byte[]> message = MessageBuilder.withPayload(new byte[0])
                .setHeader("simpSessionAttributes", sessionAttributes)
                .build();

        SessionDisconnectEvent event = new SessionDisconnectEvent(this, message, "mockSessionId", CloseStatus.NORMAL);

        listener.handleWebSocketDisconnectListener(event);

        verify(playerConnectionService).handleDisconnection("user1", "ROOM42");
        verify(gameBroadcastService).broadcastPlayerList("ROOM42");
        verifyNoMoreInteractions(playerConnectionService, gameBroadcastService);
    }

    @Test
    void handleWebSocketDisconnect_withoutSessionAttributes_logsWarningOnly() {
        Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).build();
        SessionDisconnectEvent event = new SessionDisconnectEvent(this, message, "mockSessionId", CloseStatus.NORMAL);

        listener.handleWebSocketDisconnectListener(event);

        verifyNoInteractions(playerConnectionService, gameBroadcastService);
    }
}
