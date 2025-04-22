package ch.uzh.ifi.hase.soprafs25.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class UserHandshakeHandlerTest {
    
    @Test
    void testDetermineUser() {
        UserHandshakeHandler handler = new UserHandshakeHandler();
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        WebSocketHandler wsHandler = mock(WebSocketHandler.class);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("nickname", "testUser");
        attributes.put("code", "ROOM123");

        Principal principal = handler.determineUser(request, wsHandler, attributes);
        assertNotNull(principal);
        assertEquals("testUser:ROOM123", principal.getName());
    }
}
