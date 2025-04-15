package ch.uzh.ifi.hase.soprafs25.rest.mapper;

import ch.uzh.ifi.hase.soprafs25.model.ChatMessageDTO;
import ch.uzh.ifi.hase.soprafs25.model.CreateRoomDTO;
import ch.uzh.ifi.hase.soprafs25.model.ErrorResponseDTO;
import ch.uzh.ifi.hase.soprafs25.model.RoomPostDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DTOMapperTest {

    @Test
    void testCreateRoomDTO() {
        CreateRoomDTO dto = new CreateRoomDTO("testUser", "ABC123");
        assertEquals("testUser", dto.getNickname());
        assertEquals("ABC123", dto.getRoomCode());
    }

    @Test
    void testRoomPostDTO() {
        RoomPostDTO dto = new RoomPostDTO("testUser", "ABC123");
        assertEquals("testUser", dto.getNickname());
        assertEquals("ABC123", dto.getCode());
    }

    @Test
    void testChatMessage() {
        ChatMessageDTO message = new ChatMessageDTO();
        message.setNickname("testUser");
        message.setMessage("hello");
        message.setColor("blue");
        assertEquals("testUser", message.getNickname());
        assertEquals("hello", message.getMessage());
        assertEquals("blue", message.getColor());
    }

    @Test
    void testErrorResponseDTO() {
        ErrorResponseDTO dto = new ErrorResponseDTO("error");
        assertEquals("error", dto.getMessage());
    }
}