package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.model.ChatMessageDTO;
import ch.uzh.ifi.hase.soprafs25.util.SessionUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat")
    public void createMessage(@Payload ChatMessageDTO message, Message<?> socketMessage) {
        String nickname = SessionUtil.getNickname(socketMessage);
        String code = SessionUtil.getCode(socketMessage);
        String color = SessionUtil.getColor(socketMessage);

        message.setNickname(nickname);
        message.setColor(color);
        messagingTemplate.convertAndSend("/topic/chat/" + code, message);
    }
}
