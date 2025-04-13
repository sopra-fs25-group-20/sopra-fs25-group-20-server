package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.model.ChatMessageDTO;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat")
    public void createMessage(@Payload ChatMessageDTO message,
                              Message<?> socketMessage) {

        Map<String, Object> sessionAttributes =
                (Map<String, Object>) socketMessage.getHeaders().get("simpSessionAttributes");

        if (sessionAttributes == null) {
            // This is an extra check to our check in CustomInterceptor
            throw new IllegalStateException("Missing session attributes in WebSocket message headers");
        }

        String nickname = (String) sessionAttributes.get("nickname");
        String code = (String) sessionAttributes.get("code");
        String color = (String) sessionAttributes.get("color");

        message.setNickname(nickname);
        message.setColor(color);
        messagingTemplate.convertAndSend("/topic/chat/" + code, message);
    }
}
