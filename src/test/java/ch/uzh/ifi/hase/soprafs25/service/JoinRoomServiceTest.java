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
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @Test
    void joinRoomNicknameExistsAndDisconnected_reconnects() {
        Room room = new Room();
        room.setCode("ROOM10");

        Player existing = new Player();
        existing.setNickname("bob");
        existing.setConnected(false);

        when(roomRepository.findByCode("ROOM10")).thenReturn(room);
        when(playerRepository.findByNicknameAndRoom("bob", room)).thenReturn(existing);
        when(playerRepository.save(existing)).thenReturn(existing);

        Player input = new Player();
        input.setNickname("bob");

        Player result = joinRoomService.joinRoom("ROOM10", input);

        assertEquals("bob", result.getNickname());
        verify(playerRepository).save(existing);
    }

    @Test
    void validateJoin_nicknameAvailable_noException() {
        Room room = new Room();
        room.setCode("OPEN");

        when(roomRepository.findByCode("OPEN")).thenReturn(room);
        when(playerRepository.findByNicknameAndRoom("unique", room)).thenReturn(null);

        assertDoesNotThrow(() -> joinRoomService.validateJoin("OPEN", "unique"));
    }

    @Test
    void validateJoin_nicknameAlreadyConnected_throws() {
        Room room = new Room();
        room.setCode("DUP");

        Player existing = new Player();
        existing.setNickname("dup");
        existing.setConnected(true);

        when(roomRepository.findByCode("DUP")).thenReturn(room);
        when(playerRepository.findByNicknameAndRoom("dup", room)).thenReturn(existing);

        assertThrows(NicknameAlreadyInRoomException.class, () ->
                joinRoomService.validateJoin("DUP", "dup"));
    }

    @Test
    void validateJoin_roomNotFound_throws() {
        when(roomRepository.findByCode("NOPE")).thenReturn(null);

        assertThrows(RoomNotFoundException.class, () ->
                joinRoomService.validateJoin("NOPE", "any"));
    }
}
