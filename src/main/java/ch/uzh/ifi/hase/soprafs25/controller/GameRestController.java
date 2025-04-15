package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.model.PlayerListUpdateDTO;
import ch.uzh.ifi.hase.soprafs25.service.PlayerConnectionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GameRestController {

    private final PlayerConnectionService playerConnectionService;

    public GameRestController(PlayerConnectionService playerConnectionService) {
        this.playerConnectionService = playerConnectionService;
    }

    @GetMapping("/players/{code}")
    public List<PlayerListUpdateDTO> getPlayerList(@PathVariable String code) {
        return playerConnectionService.getPlayerListDTO(code);
    }
}
