package ch.uzh.ifi.hase.soprafs25.config;

import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.service.JoinRoomService;
import ch.uzh.ifi.hase.soprafs25.service.PlayerConnectionService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Configuration
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    private final JoinRoomService joinRoomService;
    private final PlayerConnectionService playerConnectionService;

    public CustomHandshakeInterceptor(JoinRoomService joinRoomService,
                                      @Lazy PlayerConnectionService playerConnectionService) {
        this.joinRoomService = joinRoomService;
        this.playerConnectionService = playerConnectionService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {

        if (!(request instanceof ServletServerHttpRequest servletRequestWrapper)) {
            return false;
        }

        HttpServletRequest servletRequest = servletRequestWrapper.getServletRequest();
        String nickname = servletRequest.getParameter("nickname");
        String code = servletRequest.getParameter("code");

        if (!isValidHandshakeParams(nickname, code)) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        if (playerConnectionService.isOnline(nickname, code)) {
            response.setStatusCode(HttpStatus.CONFLICT);
            return false;
        }

        Player createdPlayer = joinPlayer(nickname, code);

        attributes.put("nickname", nickname);
        attributes.put("code", code);
        attributes.put("color", createdPlayer.getColor());

        playerConnectionService.markConnected(createdPlayer.getNickname(), createdPlayer.getRoom().getCode());
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // Method must be overridden, but no post handshake is needed currently
    }

    private boolean isValidHandshakeParams(String nickname, String code) {
        if (nickname == null || nickname.isBlank()) {
            return false;
        }

        if (code == null || !code.matches("^[A-Z0-9]{6}$")) {
            return false;
        }

        return true;
    }

    private Player joinPlayer(String nickname, String code) {
        Player player = new Player();
        player.setNickname(nickname);
        return joinRoomService.joinRoom(code, player);
    }
}
