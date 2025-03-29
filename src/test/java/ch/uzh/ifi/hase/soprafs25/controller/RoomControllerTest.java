package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.model.CreateRoomDTO;
import ch.uzh.ifi.hase.soprafs25.model.RoomPostDTO;
import ch.uzh.ifi.hase.soprafs25.service.CreateRoomService;
import ch.uzh.ifi.hase.soprafs25.service.JoinRoomService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class RoomControllerTest {

    @Mock
    private CreateRoomService createRoomService;

    @Mock
    private JoinRoomService joinRoomService;

    @InjectMocks
    private RoomController roomController;

    @Test
    public void testCreateRoom() {
        RoomPostDTO dto = new RoomPostDTO("testUser", null);
        Player player = new Player();
        player.setNickname("testUser");
        player.setRoom(new ch.uzh.ifi.hase.soprafs25.entity.Room());
        player.getRoom().setCode("ABC123");

        when(createRoomService.createRoom(any(Player.class))).thenReturn(player);

        CreateRoomDTO result = roomController.createRoom(dto);

        assertEquals("testUser", result.getNickname());
        assertEquals("ABC123", result.getRoomCode());
    }

    @Test
    public void testValidateRoomAndNickname() {
        RoomPostDTO dto = new RoomPostDTO("testUser", "ROOM1");
        assertDoesNotThrow(() -> roomController.validateRoomAndNickname(dto));
        verify(joinRoomService).validateJoin("ROOM1", "testUser");
    }
}