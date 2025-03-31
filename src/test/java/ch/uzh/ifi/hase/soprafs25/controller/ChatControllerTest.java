package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.model.ChatMessage;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


public class ChatControllerTest {

    @Test
    public void createMessageSendsToCorrectTopic() {
        SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
        ChatController controller = new ChatController(messagingTemplate);

        ChatMessage chatMessage = new ChatMessage();
        Map<String, Object> sessionAttributes = Map.of(
            "nickname", "testUser",
            "code", "ROOM1",
            "color", "blue"
        );
        Map<String, Object> headersMap = new HashMap<>();
        headersMap.put("simpSessionAttributes", sessionAttributes);
        Message<?> message = MessageBuilder.withPayload(new Object())
                .copyHeaders(headersMap)
                .build();
        controller.createMessage(chatMessage, message);

        verify(messagingTemplate).convertAndSend(eq("/topic/chat/ROOM1"), any(ChatMessage.class));
        assertEquals("testUser", chatMessage.getNickname());
        assertEquals("blue", chatMessage.getColor());
    }
}