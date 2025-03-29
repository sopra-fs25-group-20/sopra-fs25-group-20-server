package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.model.ChatMessage;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashMap;
import java.util.Map;


public class ChatControllerTest {

    @Test
    public void createMessageSendsToCorrectTopic() {
        SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
        ChatController controller = new ChatController(messagingTemplate);

        ChatMessage chatMessage = new ChatMessage();
        Map<String, Object> headers = new HashMap<>();
        headers.put("simpSessionAttributes", Map.of(
            "nickname", "testUser",
            "code", "ROOM1",
            "color", "blue"
        ));

        Message<?> message = mock(Message.class);
        when(message.getHeaders()).thenReturn(headers);

        controller.createMessage(chatMessage, message);

        verify(messagingTemplate).convertAndSend(eq("/topic/chat/ROOM1"), any(ChatMessage.class));
        assertEquals("testUser", chatMessage.getNickname());
        assertEquals("blue", chatMessage.getColor());
    }
}