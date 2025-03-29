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


@ExtendWith(MockitoExtension.class)
public class JoinRoomServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private JoinRoomService joinRoomService;

    @Test
    public void joinRoomSuccessful() {
        Room room = new Room();
        room.setCode("ABC123");
        Player player = new Player();
        player.setNickname("testUser");

        when(roomRepository.findByCode("ABC123")).thenReturn(room);
        when(playerRepository.findByNicknameAndRoom("testUser", room)).thenReturn(null);
        when(playerRepository.save(any(Player.class))).thenReturn(player);

        Player result = joinRoomService.joinRoom("ABC123", player);
        assertEquals("testUser", result.getNickname());
    }

    @Test
    public void joinRoomRoomNotFound() {
        when(roomRepository.findByCode("BAD123")).thenReturn(null);

        Player player = new Player();
        player.setNickname("testUser");

        assertThrows(RoomNotFoundException.class, () ->
                joinRoomService.joinRoom("BAD123", player));
    }

    @Test
    public void joinRoomNicknameExists() {
        Room room = new Room();
        room.setCode("ROOM99");
        Player existing = new Player();
        existing.setNickname("duplicate");

        when(roomRepository.findByCode("ROOM99")).thenReturn(room);
        when(playerRepository.findByNicknameAndRoom("duplicate", room)).thenReturn(existing);

        Player player = new Player();
        player.setNickname("duplicate");

        assertThrows(NicknameAlreadyInRoomException.class, () ->
                joinRoomService.joinRoom("ROOM99", player));
    }
}