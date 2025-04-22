package ch.uzh.ifi.hase.soprafs25.config;

import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.entity.Room;
import ch.uzh.ifi.hase.soprafs25.service.JoinRoomService;
import ch.uzh.ifi.hase.soprafs25.service.PlayerConnectionService;
import org.junit.jupiter.api.Test;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CustomHandshakeInterceptorTest {

    @Test
    void testInvalidRequestType() {
        CustomHandshakeInterceptor interceptor = new CustomHandshakeInterceptor(
            mock(JoinRoomService.class), mock(PlayerConnectionService.class)
        );
        boolean result = interceptor.beforeHandshake(
            mock(org.springframework.http.server.ServerHttpRequest.class),
            null, mock(WebSocketHandler.class), new HashMap<>()
        );
        assertFalse(result);
    }

    @Test
    void testMissingParams() {
        JoinRoomService joinRoomService = mock(JoinRoomService.class);
        PlayerConnectionService playerConnectionService = mock(PlayerConnectionService.class);
        CustomHandshakeInterceptor interceptor = new CustomHandshakeInterceptor(joinRoomService, playerConnectionService);

        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getParameter("nickname")).thenReturn(null);
        when(servletRequest.getParameter("code")).thenReturn(null);

        ServletServerHttpRequest request = mock(ServletServerHttpRequest.class);
        when(request.getServletRequest()).thenReturn(servletRequest);

        Map<String, Object> attributes = new HashMap<>();
        var mockResponse = mock(org.springframework.http.server.ServerHttpResponse.class);

        boolean result = interceptor.beforeHandshake(request, mockResponse, mock(WebSocketHandler.class), attributes);
        assertFalse(result);
    }

    @Test
    void testSuccessfulHandshake() {
        JoinRoomService joinRoomService = mock(JoinRoomService.class);
        PlayerConnectionService playerConnectionService = mock(PlayerConnectionService.class);
        CustomHandshakeInterceptor interceptor = new CustomHandshakeInterceptor(joinRoomService, playerConnectionService);

        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getParameter("nickname")).thenReturn("alice");
        when(servletRequest.getParameter("code")).thenReturn("ABC123");

        ServletServerHttpRequest request = mock(ServletServerHttpRequest.class);
        when(request.getServletRequest()).thenReturn(servletRequest);

        Player mockPlayer = mock(Player.class);
        Room mockRoom = mock(Room.class);

        when(mockPlayer.getNickname()).thenReturn("alice");
        when(mockPlayer.getColor()).thenReturn("#FF6B6B");
        when(mockPlayer.getRoom()).thenReturn(mockRoom);
        when(mockRoom.getCode()).thenReturn("ABC123");

        when(playerConnectionService.isOnline("alice", "ABC123")).thenReturn(false);
        when(joinRoomService.joinRoom(eq("ABC123"), any(Player.class))).thenReturn(mockPlayer);

        Map<String, Object> attributes = new HashMap<>();
        var mockResponse = mock(org.springframework.http.server.ServerHttpResponse.class);

        boolean result = interceptor.beforeHandshake(request, mockResponse, mock(WebSocketHandler.class), attributes);

        assertTrue(result);
        assertEquals("alice", attributes.get("nickname"));
        assertEquals("ABC123", attributes.get("code"));
        assertEquals("#FF6B6B", attributes.get("color"));
    }
}
