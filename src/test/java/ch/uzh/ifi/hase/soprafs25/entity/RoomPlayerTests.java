package ch.uzh.ifi.hase.soprafs25.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoomPlayerTests {

    @Test
    void testPlayerEntity() {
        Player player = new Player();
        player.setId(1L);
        player.setNickname("testUser");
        player.setColor("blue");
        player.setRole("admin");
        Room room = new Room();
        player.setRoom(room);

        assertEquals(1L, player.getId());
        assertEquals("testUser", player.getNickname());
        assertEquals("blue", player.getColor());
        assertEquals("admin", player.getRole());
        assertEquals(room, player.getRoom());
    }

    @Test
    void testRoomEntity() {
        Room room = new Room();
        room.setRoomId(1L);
        room.setCode("TESTCODE");
        room.setAdminPlayerId(2L);

        Player player = new Player();
        player.setNickname("testUser");
        room.addPlayer(player);

        assertEquals(1L, room.getRoomId());
        assertEquals("TESTCODE", room.getCode());
        assertEquals(2L, room.getAdminPlayerId());
        assertEquals(1, room.getPlayers().size());
        assertEquals(room, player.getRoom());
    }
}