package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.model.ChatMessageDTO;
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

        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
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
        controller.createMessage(chatMessageDTO, message);

        verify(messagingTemplate).convertAndSend(eq("/topic/chat/ROOM1"), any(ChatMessageDTO.class));
        assertEquals("testUser", chatMessageDTO.getNickname());
        assertEquals("blue", chatMessageDTO.getColor());
    }
}