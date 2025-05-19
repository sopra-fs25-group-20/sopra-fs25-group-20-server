package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs25.entity.Game;
import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.entity.Room;
import ch.uzh.ifi.hase.soprafs25.model.PlayerUpdateDTO;
import ch.uzh.ifi.hase.soprafs25.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs25.session.GameSessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerConnectionServiceTest {

    private PlayerConnectionService playerConnectionService;
    private PlayerRepository playerRepository;
    private RoomRepository roomRepository;
    private AuthorizationService authorizationService;
    private GameBroadcastService gameBroadcastService;

    @BeforeEach
    void setup() {
        playerRepository = mock(PlayerRepository.class);
        roomRepository = mock(RoomRepository.class);
        authorizationService = mock(AuthorizationService.class);
        gameBroadcastService = mock(GameBroadcastService.class);

        playerConnectionService = new PlayerConnectionService(
                playerRepository, roomRepository, authorizationService, gameBroadcastService
        );
    }

    @Test
    void testMarkConnected_updatesPlayer() {
        Room room = new Room();
        Player player = new Player();
        player.setConnected(false);
        player.setNickname("testUser");
        player.setId(1L);
        room.setAdminPlayerNickname("testUser");

        when(roomRepository.findByCode("ROOM123")).thenReturn(room);
        when(playerRepository.findByNicknameAndRoom("testUser", room)).thenReturn(player);

        playerConnectionService.markConnected("testUser", "ROOM123");

        assertTrue(player.isConnected());
        verify(playerRepository).save(player);
    }

    @Test
    void testMarkDisconnected_updatesPlayer() {
        Room room = new Room();
        Player player = new Player();
        player.setConnected(true);
        player.setNickname("testUser");

        when(roomRepository.findByCode("ROOM123")).thenReturn(room);
        when(playerRepository.findByNicknameAndRoom("testUser", room)).thenReturn(player);

        playerConnectionService.markDisconnected("testUser", "ROOM123");

        assertFalse(player.isConnected());
        verify(playerRepository).save(player);
    }

    @Test
    void testIsOnline_true() {
        Room room = new Room();
        Player player = new Player();
        player.setConnected(true);

        when(roomRepository.findByCode("ROOM123")).thenReturn(room);
        when(playerRepository.findByNicknameAndRoom("user", room)).thenReturn(player);

        assertTrue(playerConnectionService.isOnline("user", "ROOM123"));
    }

    @Test
    void testIsOnline_playerNotFound_returnsFalse() {
        Room room = new Room();

        when(roomRepository.findByCode("ROOM123")).thenReturn(room);
        when(playerRepository.findByNicknameAndRoom("ghost", room)).thenReturn(null);

        assertFalse(playerConnectionService.isOnline("ghost", "ROOM123"));
    }

    @Test
    void testGetPlayerListDTO_returnsDTOList() {
        Room room = new Room();
        Player player = new Player();
        player.setNickname("testUser");
        player.setColor("red");
        player.setId(2L);
        room.setPlayers(List.of(player));

        when(roomRepository.findByCode("ROOM123")).thenReturn(room);

        List<PlayerUpdateDTO> list = playerConnectionService.getPlayerListDTO("ROOM123");

        assertEquals(1, list.size());
        assertEquals("testUser", list.get(0).getNickname());
        assertEquals("red", list.get(0).getColor());
        assertFalse(list.get(0).isAdmin());
    }

    @Test
    void testGetPlayers_success() {
        Room room = new Room();
        room.setPlayers(List.of(new Player()));
        when(roomRepository.findByCode("ROOM123")).thenReturn(room);

        List<Player> players = playerConnectionService.getPlayers("ROOM123");

        assertEquals(1, players.size());
    }

    @Test
    void testGetPlayers_roomNotFound_throwsException() {
        when(roomRepository.findByCode("NO_ROOM")).thenReturn(null);

        assertThrows(IllegalStateException.class, () ->
                playerConnectionService.getPlayers("NO_ROOM"));
    }

    @Test
    void testKickPlayer_unauthorized_throws() {
        when(authorizationService.isAdmin("ROOM123", "hacker")).thenReturn(false);

        assertThrows(IllegalStateException.class, () ->
                playerConnectionService.kickPlayer("hacker", "victim", "ROOM123"));
    }

    @Test
    void testKickPlayer_targetNotFound_throws() {
        Room room = new Room();
        when(authorizationService.isAdmin("ROOM123", "admin")).thenReturn(true);
        when(roomRepository.findByCode("ROOM123")).thenReturn(room);
        when(playerRepository.findByNicknameAndRoom("ghost", room)).thenReturn(null);

        assertThrows(IllegalStateException.class, () ->
                playerConnectionService.kickPlayer("admin", "ghost", "ROOM123"));
    }

    @Test
    void testKickPlayer_authorized_success() {
        Room room = new Room();
        Player kicked = new Player();
        kicked.setNickname("target");

        when(authorizationService.isAdmin("ROOM123", "admin")).thenReturn(true);
        when(roomRepository.findByCode("ROOM123")).thenReturn(room);
        when(playerRepository.findByNicknameAndRoom("target", room)).thenReturn(kicked);

        playerConnectionService.kickPlayer("admin", "target", "ROOM123");

        verify(playerRepository).delete(kicked);
        verify(gameBroadcastService).broadcastPlayerList("ROOM123");
    }

    @Test
    void handleDisconnection_inLobby_shouldRemoveAndBroadcast() {
        Room room = new Room();

        Player p = new Player();
        p.setNickname("Alice");

        Game lobby = new Game("ROOM");
        lobby.setPhase(GamePhase.LOBBY);

        when(roomRepository.findByCode("ROOM")).thenReturn(room);
        when(playerRepository.findByNicknameAndRoom("Alice", room)).thenReturn(p);

        GameSessionManager.addGameSession(lobby);
        playerConnectionService.handleDisconnection("Alice", "ROOM");

        verify(playerRepository).delete(p);
        verify(gameBroadcastService).broadcastPlayerList("ROOM");
        GameSessionManager.removeGameSession("ROOM");
    }

    @Test
    void handleDisconnection_inOtherPhase_shouldMarkDisconnected() {
        Room room = new Room();

        Player p = new Player();
        p.setNickname("Alice2");
        p.setConnected(true);

        when(roomRepository.findByCode("ROOM2")).thenReturn(room);
        when(playerRepository.findByNicknameAndRoom("Alice2", room)).thenReturn(p);

        Game game = new Game("ROOM2");
        game.setPhase(GamePhase.GAME);
        GameSessionManager.addGameSession(game);

        playerConnectionService.handleDisconnection("Alice2", "ROOM2");

        assertFalse(p.isConnected(), "Player should be marked disconnected");
        verify(playerRepository).save(p);
        verify(gameBroadcastService, never()).broadcastPlayerList(any());
        GameSessionManager.removeGameSession("ROOM2");
    }
}
