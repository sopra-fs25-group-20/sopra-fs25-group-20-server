package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.entity.Room;
import ch.uzh.ifi.hase.soprafs25.model.PlayerUpdateDTO;
import ch.uzh.ifi.hase.soprafs25.model.TimerDTO;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class GameReadServiceTest {

    private static final String ROOM_CODE  = "ROOM123";
    private static final long   REMAIN_SEC = 30L;

    private RoomRepository    roomRepository;
    private GameTimerService  gameTimerService;
    private GameReadService   service;
    private Room              room;

    @BeforeEach
    void setup() {
        roomRepository   = mock(RoomRepository.class);
        gameTimerService = mock(GameTimerService.class);
        service          = new GameReadService(roomRepository, gameTimerService);

        room = new Room();
        room.setCode(ROOM_CODE);

        Player p1 = new Player();
        p1.setId(1L);
        p1.setNickname("alice");
        p1.setColor("#FF6B6B");
        p1.setUser(null);

        Player p2 = new Player();
        p2.setId(2L);
        p2.setNickname("bob");
        p2.setColor("#00FF00");
        p2.setUser(null);

        room.setAdminPlayerId(1L);
        room.setPlayers(List.of(p1, p2));

        when(roomRepository.findByCode(ROOM_CODE)).thenReturn(room);
    }

    @Test
    void getNicknamesInRoom_returnsNicknames() {
        List<String> nicknames = service.getNicknamesInRoom(ROOM_CODE);
        assertEquals(List.of("alice", "bob"), nicknames);
    }

    @Test
    void getPlayerCount_returnsSize() {
        int count = service.getPlayerCount(ROOM_CODE);
        assertEquals(2, count);
    }

    @Test
    void getPlayersInRoom_returnsPlayers() {
        List<Player> players = service.getPlayersInRoom(ROOM_CODE);
        assertEquals(2, players.size());
        assertEquals("alice", players.get(0).getNickname());
    }

    @Test
    void getPlayerUpdateList_marksAdmin() {
        List<PlayerUpdateDTO> updates = service.getPlayerUpdateList(ROOM_CODE);

        PlayerUpdateDTO first = updates.get(0);
        assertAll(
                () -> assertEquals("alice",    first.getNickname()),
                () -> assertEquals("#FF6B6B",  first.getColor()),
                () -> assertTrue(first.isAdmin()),
                () -> assertNull(first.getUser())
        );

        PlayerUpdateDTO second = updates.get(1);
        assertAll(
                () -> assertEquals("bob",      second.getNickname()),
                () -> assertFalse(second.isAdmin())
        );
    }

    @Test
    void getTimer_whenActive_returnsRemaining() {
        when(gameTimerService.isTimerActive(anyString())).thenReturn(true);
        when(gameTimerService.getRemainingSeconds(anyString()))
                .thenReturn(Optional.of(REMAIN_SEC));

        TimerDTO timer = service.getTimer(ROOM_CODE, GamePhase.GAME);
        assertEquals((int) REMAIN_SEC, timer.getRemainingSeconds());
    }

    @Test
    void getTimer_whenInactive_returnsNull() {
        when(gameTimerService.isTimerActive(anyString())).thenReturn(false);

        TimerDTO timer = service.getTimer(ROOM_CODE, GamePhase.LOBBY);
        assertNull(timer.getRemainingSeconds());
    }
}
