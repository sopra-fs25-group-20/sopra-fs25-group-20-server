package ch.uzh.ifi.hase.soprafs25.config;

import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.service.JoinRoomService;
import org.springframework.context.annotation.Configuration;
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

    public CustomHandshakeInterceptor(JoinRoomService joinRoomService) {
        this.joinRoomService = joinRoomService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            String nickname = servletRequest.getParameter("nickname");
            String code = servletRequest.getParameter("code");

            if (nickname == null || code == null) {
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                return false;
            }
            attributes.put("nickname", nickname);
            attributes.put("code", code);

            Player player = new Player();
            player.setNickname(nickname);
            Player createdPlayer = joinRoomService.joinRoom(code, player);

            attributes.put("color", createdPlayer.getColor());
            return true;
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
    }
}
