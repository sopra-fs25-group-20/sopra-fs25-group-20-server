package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.entity.Room;
import ch.uzh.ifi.hase.soprafs25.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorizationServiceTest {

    private AuthorizationService authorizationService;
    private RoomRepository roomRepository;
    private PlayerRepository playerRepository;

    @BeforeEach
    void setup() {
        roomRepository = mock(RoomRepository.class);
        playerRepository = mock(PlayerRepository.class);
        authorizationService = new AuthorizationService(roomRepository, playerRepository);
    }

    @Test
    void testIsAdmin_true() {
        Room room = new Room();
        room.setAdminPlayerNickname("admin");
        Player player = new Player();
        player.setNickname("admin");

        when(roomRepository.findByCode("ROOM123")).thenReturn(room);
        when(playerRepository.findByNicknameAndRoom("admin", room)).thenReturn(player);

        assertTrue(authorizationService.isAdmin("ROOM123", "admin"));
    }

    @Test
    void testIsAdmin_false() {
        Room room = new Room();
        room.setAdminPlayerNickname("admin");
        Player player = new Player();
        player.setId(1L);

        when(roomRepository.findByCode("ROOM123")).thenReturn(room);
        when(playerRepository.findByNicknameAndRoom("testUser", room)).thenReturn(player);

        assertFalse(authorizationService.isAdmin("ROOM123", "testUser"));
    }

    @Test
    void testIsAdmin_playerNotFound() {
        Room room = new Room();
        when(roomRepository.findByCode("ROOM123")).thenReturn(room);
        when(playerRepository.findByNicknameAndRoom("testUser", room)).thenReturn(null);

        Exception ex = assertThrows(IllegalStateException.class, () ->
                authorizationService.isAdmin("ROOM123", "testUser")
        );
        assertEquals("Player 'testUser' not found in room 'ROOM123'", ex.getMessage());

    }

    @Test
    void testIsAdmin_roomNotFound() {
        when(roomRepository.findByCode("ROOM123")).thenReturn(null);

        Exception ex = assertThrows(IllegalStateException.class, () ->
                authorizationService.isAdmin("ROOM123", "nick")
        );
        assertTrue(ex.getMessage().contains("Room not found"));
    }
}
