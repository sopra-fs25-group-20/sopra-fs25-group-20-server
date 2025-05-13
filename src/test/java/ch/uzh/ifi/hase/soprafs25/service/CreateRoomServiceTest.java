package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.entity.Room;
import ch.uzh.ifi.hase.soprafs25.entity.User;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs25.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateRoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private JoinRoomService joinRoomService;

    @Mock
    private GameService gameService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreateRoomService createRoomService;

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player();
        player.setNickname("TestUser");

        Room room = new Room();
        room.setCode("ROOM1");

        when(roomRepository.save(any(Room.class))).thenReturn(room);
        doNothing().when(gameService).createGame(eq("ROOM1"));
    }

    @Test
    void createRoom_withoutToken_savesAndJoins() {
        Player joined = new Player();
        joined.setId(42L);
        joined.setNickname("TestUser");

        when(joinRoomService.joinRoom(eq("ROOM1"), any(Player.class))).thenReturn(joined);

        Player result = createRoomService.createRoom(player, null);

        assertEquals("TestUser", result.getNickname());
        verify(roomRepository, times(2)).save(any(Room.class));
        verify(joinRoomService).joinRoom("ROOM1", player);
        verify(gameService).createGame("ROOM1");
    }

    @Test
    void createRoom_withBearerToken_attachesUser() {
        User user = new User();                    // no setId(), just use the object
        when(userRepository.findByToken("token123"))
                .thenReturn(user);

        Player joined = new Player();
        joined.setId(99L);
        joined.setNickname("TestUser");
        joined.setUser(user);

        when(joinRoomService.joinRoom(eq("ROOM1"), any(Player.class)))
                .thenReturn(joined);

        Player result = createRoomService.createRoom(player, "Bearer token123");

        assertSame(user, result.getUser());
        verify(userRepository).findByToken("token123");
        verify(roomRepository, times(2)).save(any(Room.class));
        verify(joinRoomService).joinRoom("ROOM1", player);
        verify(gameService).createGame("ROOM1");
    }
}
