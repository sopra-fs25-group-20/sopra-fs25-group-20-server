package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.entity.Room;
import ch.uzh.ifi.hase.soprafs25.model.CreateRoomDTO;
import ch.uzh.ifi.hase.soprafs25.model.RoomPostDTO;
import ch.uzh.ifi.hase.soprafs25.service.CreateRoomService;
import ch.uzh.ifi.hase.soprafs25.service.JoinRoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class RoomControllerTest {

    @Mock
    private CreateRoomService createRoomService;

    @Mock
    private JoinRoomService joinRoomService;

    @InjectMocks
    private RoomController roomController;

    @Test
    void testCreateRoom() {
        RoomPostDTO dto = new RoomPostDTO("testUser", null);
        Player player = new Player();
        player.setNickname("testUser");
        Room room = new Room();
        room.setCode("ABC123");
        player.setRoom(room);

        when(createRoomService.createRoom(any(Player.class), isNull()))
                .thenReturn(player);

        CreateRoomDTO result = roomController.createRoom(null, dto);

        assertEquals("testUser", result.getNickname());
        assertEquals("ABC123",  result.getRoomCode());
    }

    @Test
    void testValidateRoomAndNickname() {
        RoomPostDTO dto = new RoomPostDTO("testUser", "ROOM1");
        assertDoesNotThrow(() -> roomController.validateRoomAndNickname(dto));
        verify(joinRoomService).validateJoin("ROOM1", "testUser");
    }
}
