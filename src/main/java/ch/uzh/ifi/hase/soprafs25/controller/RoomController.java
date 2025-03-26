package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.model.CreateRoomDTO;
import ch.uzh.ifi.hase.soprafs25.service.CreateRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class RoomController {

    private final CreateRoomService createRoomService;

    public RoomController(CreateRoomService createRoomService) {
        this.createRoomService = createRoomService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public CreateRoomDTO createRoom(@RequestBody Player player) {
        Player createdPlayer = createRoomService.createRoom(player);

        return new CreateRoomDTO(
                createdPlayer.getNickname(),
                createdPlayer.getColor(),
                createdPlayer.getRoom().getCode()
        );
    }
}