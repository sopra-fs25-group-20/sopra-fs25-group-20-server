package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.model.ChatMessageDTO;
import ch.uzh.ifi.hase.soprafs25.service.GameBroadcastService;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

class ChatControllerTest {

    @Test
    void createMessageSendsToCorrectTopic() {
        GameBroadcastService mockBroadcastService = mock(GameBroadcastService.class);
        ChatController controller = new ChatController(mockBroadcastService);

        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setMessage("hello");

        Map<String, Object> sessionAttributes = Map.of(
                "nickname", "testUser",
                "code", "ROOM1",
                "color", "blue"
        );

        Map<String, Object> headersMap = new HashMap<>();
        headersMap.put("simpSessionAttributes", sessionAttributes);

        Message<?> socketMessage = MessageBuilder.withPayload(new Object())
                .copyHeaders(headersMap)
                .build();

        controller.createMessage(chatMessageDTO, socketMessage);

        verify(mockBroadcastService).broadcastChatMessage("ROOM1", "testUser", "hello", "blue");
    }
}
