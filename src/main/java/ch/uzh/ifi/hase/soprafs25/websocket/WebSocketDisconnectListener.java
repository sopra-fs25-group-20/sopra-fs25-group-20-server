package ch.uzh.ifi.hase.soprafs25.websocket;

import ch.uzh.ifi.hase.soprafs25.service.PlayerConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

@Component
public class WebSocketDisconnectListener {

    private static final Logger log = LoggerFactory.getLogger(WebSocketDisconnectListener.class);

    private final PlayerConnectionService playerConnectionService;

    public WebSocketDisconnectListener(PlayerConnectionService playerConnectionService) {
        this.playerConnectionService = playerConnectionService;
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        if (sessionAttributes != null) {
            String nickname = (String) sessionAttributes.get("nickname");
            String code = (String) sessionAttributes.get("code");
            String color = (String) sessionAttributes.get("color");

            log.info("User disconnected: nickname={}, code={}, color={}", nickname, code, color);
            playerConnectionService.markDisconnected(nickname, code);
        } else {
            log.warn("Disconnect with no session attributes found");
        }
    }
}

