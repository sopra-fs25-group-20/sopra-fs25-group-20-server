// src/test/java/ch/uzh/ifi/hase/soprafs25/service/GameReadServiceTest.java
package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.entity.Room;
import ch.uzh.ifi.hase.soprafs25.model.PlayerUpdateDTO;
import ch.uzh.ifi.hase.soprafs25.model.TimerDTO;
import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
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

    private GameReadService service;

    @BeforeEach
    void setUp() {
        RoomRepository roomRepo       = mock(RoomRepository.class);
        GameTimerService timerService = mock(GameTimerService.class);
        service                       = new GameReadService(roomRepo, timerService);

        Room room = new Room();
        room.setCode(ROOM_CODE);

        Player p1 = new Player();
        p1.setId(1L);
        p1.setNickname("alice");
        p1.setColor("#FF6B6B");

        Player p2 = new Player();
        p2.setId(2L);
        p2.setNickname("bob");
        p2.setColor("#00FF00");

        room.setAdminPlayerId(1L);
        room.setPlayers(List.of(p1, p2));

        when(roomRepo.findByCode(ROOM_CODE)).thenReturn(room);
    }

    @Test
    void getNicknamesInRoom_returnsNicknames() {
        assertEquals(List.of("alice", "bob"), service.getNicknamesInRoom(ROOM_CODE));
    }

    @Test
    void getPlayerCount_returnsSize() {
        assertEquals(2, service.getPlayerCount(ROOM_CODE));
    }

    @Test
    void getPlayersInRoom_returnsPlayers() {
        assertEquals(2, service.getPlayersInRoom(ROOM_CODE).size());
    }

    @Test
    void getPlayerUpdateList_marksAdminCorrectly() {
        List<PlayerUpdateDTO> updates = service.getPlayerUpdateList(ROOM_CODE);
        assertAll(
                () -> assertEquals("alice", updates.get(0).getNickname()),
                () -> assertEquals("#FF6B6B", updates.get(0).getColor()),
                () -> assertTrue(updates.get(0).isAdmin()),
                () -> assertEquals("bob", updates.get(1).getNickname()),
                () -> assertEquals("#00FF00", updates.get(1).getColor()),
                () -> assertFalse(updates.get(1).isAdmin())
        );
    }

    @Test
    void getTimer_whenActive_returnsRemaining() {
        GameTimerService timerSvc = mock(GameTimerService.class);
        when(timerSvc.isTimerActive(anyString())).thenReturn(true);
        when(timerSvc.getRemainingSeconds(anyString())).thenReturn(Optional.of(REMAIN_SEC));

        service = new GameReadService(mock(RoomRepository.class), timerSvc);
        TimerDTO timer = service.getTimer(ROOM_CODE, GamePhase.GAME);
        assertEquals((int) REMAIN_SEC, timer.getRemainingSeconds());
    }

    @Test
    void getTimer_whenInactive_returnsNull() {
        GameTimerService timerSvc = mock(GameTimerService.class);
        when(timerSvc.isTimerActive(anyString())).thenReturn(false);

        service = new GameReadService(mock(RoomRepository.class), timerSvc);
        assertNull(service.getTimer(ROOM_CODE, GamePhase.LOBBY).getRemainingSeconds());
    }
}
