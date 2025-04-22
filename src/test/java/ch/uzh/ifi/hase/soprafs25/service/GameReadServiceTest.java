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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameReadServiceTest {

    private RoomRepository roomRepository;
    private GameReadService gameReadService;
    private Room mockRoom;

    @BeforeEach
    void setup() {
        roomRepository = mock(RoomRepository.class);
        gameReadService = new GameReadService(roomRepository);

        mockRoom = new Room();
        mockRoom.setCode("ROOM123");

        Player player1 = new Player();
        player1.setNickname("alice");
        player1.setColor("#FF6B6B");
        player1.setRoom(mockRoom);

        Player player2 = new Player();
        player2.setNickname("bob");
        player2.setColor("#6BCB77");
        player2.setRoom(mockRoom);

        mockRoom.setPlayers(List.of(player1, player2));

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
        assertEquals(2, players.size());
        assertEquals("alice", players.get(0).getNickname());
        assertEquals("#FF6B6B", players.get(0).getColor());
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
}
