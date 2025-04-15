package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.entity.Room;
import ch.uzh.ifi.hase.soprafs25.exceptions.NicknameAlreadyInRoomException;
import ch.uzh.ifi.hase.soprafs25.exceptions.RoomNotFoundException;
import ch.uzh.ifi.hase.soprafs25.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class JoinRoomServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private GameBroadcastService gameBroadcastService;

    @InjectMocks
    private JoinRoomService joinRoomService;

    @Test
    void joinRoomSuccessful() {
        Room room = new Room();
        room.setCode("ABC123");
        Player player = new Player();
        player.setNickname("testUser");

        when(roomRepository.findByCode("ABC123")).thenReturn(room);
        when(playerRepository.findByNicknameAndRoom("testUser", room)).thenReturn(null);
        when(playerRepository.save(any(Player.class))).thenReturn(player);

        doNothing().when(gameBroadcastService).broadcastPlayerList("ABC123");

        Player result = joinRoomService.joinRoom("ABC123", player);
        assertEquals("testUser", result.getNickname());
        verify(gameBroadcastService).broadcastPlayerList("ABC123");
    }

    @Test
    void joinRoomRoomNotFound() {
        when(roomRepository.findByCode("BAD123")).thenReturn(null);

        Player player = new Player();
        player.setNickname("testUser");

        assertThrows(RoomNotFoundException.class, () ->
                joinRoomService.joinRoom("BAD123", player));
    }

    @Test
    void joinRoomNicknameExistsAndConnected() {
        Room room = new Room();
        room.setCode("ROOM99");
        Player existing = new Player();
        existing.setNickname("duplicate");
        existing.setConnected(true);

        when(roomRepository.findByCode("ROOM99")).thenReturn(room);
        when(playerRepository.findByNicknameAndRoom("duplicate", room)).thenReturn(existing);

        Player player = new Player();
        player.setNickname("duplicate");

        assertThrows(NicknameAlreadyInRoomException.class, () ->
                joinRoomService.joinRoom("ROOM99", player));
    }
}