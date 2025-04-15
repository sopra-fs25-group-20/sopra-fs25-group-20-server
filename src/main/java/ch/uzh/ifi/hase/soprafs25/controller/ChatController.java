package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.model.ChatMessageDTO;
import ch.uzh.ifi.hase.soprafs25.service.GameBroadcastService;
import ch.uzh.ifi.hase.soprafs25.util.SessionUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;


@Controller
public class ChatController {

    private final GameBroadcastService gameBroadcastService;

    public ChatController(GameBroadcastService gameBroadcastService) {
        this.gameBroadcastService = gameBroadcastService;
    }

    @MessageMapping("/chat")
    public void createMessage(@Payload ChatMessageDTO chatMessageDTO, Message<?> socketMessage) {
        String code = SessionUtil.getCode(socketMessage);
        String nickname = SessionUtil.getNickname(socketMessage);
        String message = chatMessageDTO.getMessage();
        String color = SessionUtil.getColor(socketMessage);

        gameBroadcastService.broadcastChatMessage(code, nickname, message, color);
    }
}
