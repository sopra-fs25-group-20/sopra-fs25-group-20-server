package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.entity.Room;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateRoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private JoinRoomService joinRoomService;

    @Mock
    private GameService gameService;

    @InjectMocks
    private CreateRoomService createRoomService;

    @Test
    void testCreateRoom() {
        Player player = new Player();
        Room room = new Room();
        room.setCode("ROOM1");

        Player joinedPlayer = new Player();
        joinedPlayer.setId(1L);
        joinedPlayer.setNickname("TestUser");
        Room joinedRoom = new Room();
        joinedRoom.setCode("ROOM1");
        joinedPlayer.setRoom(joinedRoom);

        when(roomRepository.save(any(Room.class))).thenReturn(room);
        when(joinRoomService.joinRoom(eq("ROOM1"), any(Player.class))).thenReturn(joinedPlayer);

        doNothing().when(gameService).createGame("ROOM1");

        Player result = createRoomService.createRoom(player);

        assertEquals("TestUser", result.getNickname());
        verify(roomRepository, times(2)).save(any(Room.class));
        verify(joinRoomService).joinRoom("ROOM1", player);
        verify(gameService).createGame("ROOM1"); // ✅ kontrol
    }
}
