package ch.uzh.ifi.hase.soprafs25.config;

import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.entity.User;
import ch.uzh.ifi.hase.soprafs25.repository.UserRepository;
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
    private final UserRepository userRepository;

    public CustomHandshakeInterceptor(@Lazy JoinRoomService joinRoomService,
                                      @Lazy PlayerConnectionService playerConnectionService,
                                      UserRepository userRepository) {
        this.joinRoomService = joinRoomService;
        this.playerConnectionService = playerConnectionService;
        this.userRepository = userRepository;
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
        String token = servletRequest.getParameter("token");

        if (!isValidHandshakeParams(nickname, code)) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        User associatedUser = null;
        if (token != null && !token.isBlank()) {
            associatedUser = userRepository.findByToken(token);

            if (associatedUser == null) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;
            }
        }

        if (playerConnectionService.isOnline(nickname, code)) {
            response.setStatusCode(HttpStatus.CONFLICT);
            return false;
        }

        Player createdPlayer = joinPlayer(nickname, code, associatedUser);

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
        return nickname != null
                && !nickname.isBlank()
                && code != null
                && code.matches("^[A-Z0-9]{6}$");
    }


    private Player joinPlayer(String nickname, String code, User associatedUser) {
        Player player = new Player();
        player.setNickname(nickname);
        if (associatedUser != null) {
            player.setUser(associatedUser);
        }
        return joinRoomService.joinRoom(code, player);
    }
}