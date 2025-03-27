package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.model.CreateRoomDTO;
import ch.uzh.ifi.hase.soprafs25.model.RoomPostDTO;
import ch.uzh.ifi.hase.soprafs25.service.CreateRoomService;
import ch.uzh.ifi.hase.soprafs25.service.JoinRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class RoomController {

    private final CreateRoomService createRoomService;
    private final JoinRoomService joinRoomService;

    public RoomController(CreateRoomService createRoomService,
                          JoinRoomService joinRoomService) {
        this.createRoomService = createRoomService;
        this.joinRoomService = joinRoomService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateRoomDTO createRoom(@RequestBody RoomPostDTO roomPostDTO) {
        Player player = new Player();
        player.setNickname(roomPostDTO.getNickname());

        Player createdPlayer = createRoomService.createRoom(player);

        return new CreateRoomDTO(
                createdPlayer.getNickname(),
                createdPlayer.getColor(),
                createdPlayer.getRoom().getCode()
        );
    }

    @PostMapping("/validate/{code}")
    @ResponseStatus(HttpStatus.OK)
    public void validateRoomAndNickname(@PathVariable String code,
                                         @RequestBody RoomPostDTO roomPostDTO) {
        String nickname = roomPostDTO.getNickname();
        joinRoomService.validateJoin(code, nickname);
    }
}