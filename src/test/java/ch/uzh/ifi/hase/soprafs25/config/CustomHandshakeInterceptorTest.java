package ch.uzh.ifi.hase.soprafs25.config;

import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.entity.Room;
import ch.uzh.ifi.hase.soprafs25.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs25.service.JoinRoomService;
import ch.uzh.ifi.hase.soprafs25.service.PlayerConnectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


class CustomHandshakeInterceptorTest {

    private JoinRoomService joinRoomService;
    private PlayerConnectionService playerConnectionService;
    private UserRepository userRepository;
    private CustomHandshakeInterceptor interceptor;

    @BeforeEach
    void setUp() {
        joinRoomService = mock(JoinRoomService.class);
        playerConnectionService = mock(PlayerConnectionService.class);
        userRepository = mock(UserRepository.class);
        interceptor = new CustomHandshakeInterceptor(
                joinRoomService,
                playerConnectionService,
                userRepository
        );
    }

    @Test
    @DisplayName("should reject non-HTTP requests")
    void testInvalidRequestType() {
        boolean result = interceptor.beforeHandshake(
                mock(org.springframework.http.server.ServerHttpRequest.class),
                mock(ServerHttpResponse.class),
                mock(WebSocketHandler.class),
                new HashMap<>()
        );
        assertFalse(result);
    }

    @Test
    @DisplayName("should reject when nickname or code missing")
    void testMissingParams() {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getParameter("nickname")).thenReturn(null);
        when(servletRequest.getParameter("code")).thenReturn(null);

        ServletServerHttpRequest request = mock(ServletServerHttpRequest.class);
        when(request.getServletRequest()).thenReturn(servletRequest);

        ServerHttpResponse response = mock(ServerHttpResponse.class);
        Map<String, Object> attributes = new HashMap<>();

        boolean result = interceptor.beforeHandshake(request, response, mock(WebSocketHandler.class), attributes);

        assertFalse(result);
        verify(response).setStatusCode(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("should succeed when params valid and no token provided")
    void testSuccessfulHandshakeWithoutToken() {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getParameter("nickname")).thenReturn("alice");
        when(servletRequest.getParameter("code")).thenReturn("ABC123");
        when(servletRequest.getParameter("token")).thenReturn(null);

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

        ServerHttpResponse response = mock(ServerHttpResponse.class);
        Map<String, Object> attributes = new HashMap<>();

        boolean result = interceptor.beforeHandshake(request, response, mock(WebSocketHandler.class), attributes);

        assertTrue(result);
        assertEquals("alice", attributes.get("nickname"));
        assertEquals("ABC123", attributes.get("code"));
        assertEquals("#FF6B6B", attributes.get("color"));
        verify(playerConnectionService).markConnected("alice", "ABC123");
    }

    @Test
    @DisplayName("should reject when token is invalid")
    void testUnauthorizedToken() {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getParameter("nickname")).thenReturn("alice");
        when(servletRequest.getParameter("code")).thenReturn("ABC123");
        when(servletRequest.getParameter("token")).thenReturn("bad-token");

        ServletServerHttpRequest request = mock(ServletServerHttpRequest.class);
        when(request.getServletRequest()).thenReturn(servletRequest);

        when(userRepository.findByToken("bad-token")).thenReturn(null);

        ServerHttpResponse response = mock(ServerHttpResponse.class);
        Map<String, Object> attributes = new HashMap<>();

        boolean result = interceptor.beforeHandshake(request, response, mock(WebSocketHandler.class), attributes);

        assertFalse(result);
        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("should reject when player already online")
    void testConflictWhenAlreadyOnline() {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getParameter("nickname")).thenReturn("alice");
        when(servletRequest.getParameter("code")).thenReturn("ABC123");
        when(servletRequest.getParameter("token")).thenReturn("");

        ServletServerHttpRequest request = mock(ServletServerHttpRequest.class);
        when(request.getServletRequest()).thenReturn(servletRequest);

        when(playerConnectionService.isOnline("alice", "ABC123")).thenReturn(true);

        ServerHttpResponse response = mock(ServerHttpResponse.class);
        Map<String, Object> attributes = new HashMap<>();

        boolean result = interceptor.beforeHandshake(request, response, mock(WebSocketHandler.class), attributes);

        assertFalse(result);
        verify(response).setStatusCode(HttpStatus.CONFLICT);
    }
}
