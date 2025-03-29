package ch.uzh.ifi.hase.soprafs25.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DTOMapperTest {

    @Test
    public void testCreateRoomDTO() {
        CreateRoomDTO dto = new CreateRoomDTO("testUser", "ABC123");
        assertEquals("testUser", dto.getNickname());
        assertEquals("ABC123", dto.getRoomCode());
    }

    @Test
    public void testRoomPostDTO() {
        RoomPostDTO dto = new RoomPostDTO("testUser", "ABC123");
        assertEquals("testUser", dto.getNickname());
        assertEquals("ABC123", dto.getCode());
    }

    @Test
    public void testChatMessage() {
        ChatMessage message = new ChatMessage();
        message.setNickname("testUser");
        message.setMessage("hello");
        message.setColor("blue");
        assertEquals("testUser", message.getNickname());
        assertEquals("hello", message.getMessage());
        assertEquals("blue", message.getColor());
    }

    @Test
    public void testErrorResponseDTO() {
        ErrorResponseDTO dto = new ErrorResponseDTO("error");
        assertEquals("error", dto.getMessage());
    }
}