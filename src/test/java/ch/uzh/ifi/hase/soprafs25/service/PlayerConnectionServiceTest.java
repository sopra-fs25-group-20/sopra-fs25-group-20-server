package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.entity.Room;
import ch.uzh.ifi.hase.soprafs25.model.PlayerListUpdateDTO;
import ch.uzh.ifi.hase.soprafs25.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import java.util.List;

class PlayerConnectionServiceTest {

    private PlayerConnectionService playerConnectionService;
    private PlayerRepository playerRepository;
    private RoomRepository roomRepository;
    private SimpMessagingTemplate messagingTemplate;
    private AuthorizationService authorizationService;

    @BeforeEach
    void setup() {
        playerRepository = mock(PlayerRepository.class);
        roomRepository = mock(RoomRepository.class);
        messagingTemplate = mock(SimpMessagingTemplate.class);
        authorizationService = mock(AuthorizationService.class);

        playerConnectionService = new PlayerConnectionService(
                playerRepository, roomRepository, messagingTemplate, authorizationService
        );
    }

    @Test
    void testMarkConnected() {
        Player player = new Player();
        player.setConnected(false);
        player.setNickname("testUser");
        Room room = new Room();
        when(roomRepository.findByCode("ROOM123")).thenReturn(room);
        when(playerRepository.findByNicknameAndRoom("testUser", room)).thenReturn(player);

        playerConnectionService.markConnected("testUser", "ROOM123");

        assertTrue(player.isConnected());
        verify(playerRepository, times(1)).save(player);
    }

    @Test
    void testKickPlayer_authorized() {
        Room room = new Room();
        Player kicked = new Player();
        kicked.setNickname("testUser");

        when(authorizationService.isAdmin("ROOM123", "admin")).thenReturn(true);
        when(roomRepository.findByCode("ROOM123")).thenReturn(room);
        when(playerRepository.findByNicknameAndRoom("testUser", room)).thenReturn(kicked);

        playerConnectionService.kickPlayer("admin", "testUser", "ROOM123");

        verify(playerRepository).delete(kicked);
        verify(messagingTemplate).convertAndSend(eq("/topic/players/ROOM123"), any(List.class));

    }

    @Test
    void testGetPlayerListDTO() {
        Room room = new Room();
        Player player = new Player();
        player.setNickname("testUser");
        player.setColor("red");
        room.setPlayers(List.of(player));

        when(roomRepository.findByCode("ROOM123")).thenReturn(room);

        List<PlayerListUpdateDTO> list = playerConnectionService.getPlayerListDTO("ROOM123");

        assertEquals(1, list.size());
        assertEquals("testUser", list.get(0).getNickname());
        assertEquals("red", list.get(0).getColor());
    }
}
