package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;
import ch.uzh.ifi.hase.soprafs25.entity.*;
import ch.uzh.ifi.hase.soprafs25.model.*;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs25.session.GameSessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameReadServiceTest {

    private RoomRepository roomRepository;
    private GameTimerService gameTimerService;
    private GameReadService gameReadService;
    private Room mockRoom;

    @BeforeEach
    void setup() {
        roomRepository = mock(RoomRepository.class);
        gameTimerService = mock(GameTimerService.class);
        gameReadService = new GameReadService(roomRepository, gameTimerService);

        mockRoom = new Room();
        mockRoom.setCode("ROOM123");

        User user1 = new User();
        user1.setUsername("alice123");
        user1.setToken("token");
        user1.setWins(10);
        user1.setDefeats(9);
        user1.setGames(19);
        user1.setCurrentStreak(5);
        user1.setHighestStreak(8);

        Player player1 = new Player();
        player1.setId(1L);
        player1.setNickname("alice");
        player1.setColor("#FF6B6B");
        player1.setRoom(mockRoom);
        player1.setUser(user1);

        Player player2 = new Player();
        player2.setId(2L);
        player2.setNickname("bob");
        player2.setColor("#6BCB77");
        player2.setRoom(mockRoom);
        player2.setUser(null);          // Guest

        mockRoom.setPlayers(List.of(player1, player2));
        mockRoom.setAdminPlayerId(1L);

        when(roomRepository.findByCode("ROOM123")).thenReturn(mockRoom);
    }

    @Test
    void testGetGamePhase() {
        Game game = new Game("ROOM123");
        game.setPhase(GamePhase.VOTE);
        GameSessionManager.addGameSession(game);

        GamePhaseDTO dto = gameReadService.getGamePhase("ROOM123");
        assertEquals("vote", dto.getPhase());
    }

    @Test
    void testGetGameSettings() {
        Game game = new Game("ROOM123");
        GameSessionManager.addGameSession(game);

        GameSettingsDTO dto = gameReadService.getGameSettings("ROOM123");
        assertEquals(30, dto.getVotingTimer());
        assertEquals(300, dto.getGameTimer());
        assertEquals(9, dto.getImageCount());
        assertEquals("europe", dto.getImageRegion());
    }

    @Test
    void testGetGameResult() {
        Game game = new Game("ROOM123");
        game.setPhase(GamePhase.SUMMARY);
        game.setGameResult(1, "alice", PlayerRole.SPY);
        game.assignRoles(List.of("alice", "bob"));
        game.setHighlightedImageIndex(2);

        GameSessionManager.addGameSession(game);

        GameResultDTO dto = gameReadService.getGameResult("ROOM123");
        assertEquals(2, dto.getHighlightedImageIndex());
        assertEquals("alice", dto.getVotedNickname());
        assertEquals(PlayerRole.SPY, dto.getWinnerRole());
    }

    @Test
    void testGetNicknamesInRoom() {
        List<String> nicknames = gameReadService.getNicknamesInRoom("ROOM123");
        assertEquals(List.of("alice", "bob"), nicknames);
    }

    @Test
    void testGetPlayerUpdateList() {
        List<PlayerUpdateDTO> players = gameReadService.getPlayerUpdateList("ROOM123");

        PlayerUpdateDTO p1 = players.get(0);
        assertEquals("alice", p1.getNickname());
        assertEquals("#FF6B6B", p1.getColor());
        assertTrue(p1.isAdmin());
        assertNotNull(p1.getUser());
        assertEquals("alice123", p1.getUser().getUsername());

        PlayerUpdateDTO p2 = players.get(1);
        assertEquals("bob", p2.getNickname());
        assertEquals("#6BCB77", p2.getColor());
        assertFalse(p2.isAdmin());
        assertNull(p2.getUser());
    }

    @Test
    void testGetPlayerCount() {
        int count = gameReadService.getPlayerCount("ROOM123");
        assertEquals(2, count);
    }

    @Test
    void testGetPlayerRole() {
        Game game = new Game("ROOM123");
        game.assignRoles(List.of("alice", "bob"));
        GameSessionManager.addGameSession(game);

        PlayerRole role = gameReadService.getPlayerRole("ROOM123", "alice");
        assertNotNull(role);
    }

    @Test
    void testGetPersonalizedImageIndex() {
        Game game = new Game("ROOM123");
        game.assignRoles(List.of("alice", "bob"));
        game.setHighlightedImageIndex(3);
        GameSessionManager.addGameSession(game);

        Map<String, PlayerRole> roles = game.getRoles();

        roles.forEach((nickname, role) -> {
            Integer result = gameReadService.getPersonalizedImageIndex("ROOM123", nickname);
            if (role == PlayerRole.INNOCENT) {
                assertEquals(3, result, "Expected highlighted index for INNOCENT player");
            } else {
                assertEquals(-1, result, "Expected -1 for SPY player");
            }
        });
    }

    @Test
    void testGetTimerInactive() {
        when(gameTimerService.isTimerActive("ROOM123_vote")).thenReturn(false);

        TimerDTO dto = gameReadService.getTimer("ROOM123", GamePhase.VOTE);
        assertNull(dto.getRemainingSeconds());

        verify(gameTimerService).isTimerActive("ROOM123_vote");
        verify(gameTimerService, never()).getRemainingSeconds(any());
    }

    @Test
    void testGetTimerActiveWithRemaining() {
        when(gameTimerService.isTimerActive("ROOM123_game")).thenReturn(true);
        when(gameTimerService.getRemainingSeconds("ROOM123_game"))
                .thenReturn(Optional.of(42L));

        TimerDTO dto = gameReadService.getTimer("ROOM123", GamePhase.GAME);
        assertEquals(42, dto.getRemainingSeconds());

        verify(gameTimerService).getRemainingSeconds("ROOM123_game");
    }

    @Test
    void testGetTimerActiveNoRemaining() {
        // Normally inactive timers would be caught here, but we test the 2nd check
        when(gameTimerService.isTimerActive("ROOM123_game")).thenReturn(true);
        when(gameTimerService.getRemainingSeconds("ROOM123_game"))
                .thenReturn(Optional.empty());

        TimerDTO dto = gameReadService.getTimer("ROOM123", GamePhase.GAME);
        assertEquals(0, dto.getRemainingSeconds());

        verify(gameTimerService).getRemainingSeconds("ROOM123_game");
    }

    @Test
    void testGetPlayersInRoom() {
        List<Player> players = gameReadService.getPlayersInRoom("ROOM123");
        assertSame(mockRoom.getPlayers(), players);
    }
}