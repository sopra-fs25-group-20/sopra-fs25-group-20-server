package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.model.GamePhaseDTO;
import ch.uzh.ifi.hase.soprafs25.model.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs25.model.PlayerListUpdateDTO;
import ch.uzh.ifi.hase.soprafs25.service.GameReadService;
import ch.uzh.ifi.hase.soprafs25.service.PlayerConnectionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GameRestController {

    private final PlayerConnectionService playerConnectionService;
    private final GameReadService gameReadService;

    public GameRestController(PlayerConnectionService playerConnectionService, GameReadService gameReadService) {
        this.playerConnectionService = playerConnectionService;
        this.gameReadService = gameReadService;
    }

    @GetMapping("/players/{code}")
    public List<PlayerListUpdateDTO> getPlayerList(@PathVariable String code) {
        return playerConnectionService.getPlayerListDTO(code);
    }

    @GetMapping("/phase/{code}")
    public GamePhaseDTO getGamePhase(@PathVariable String code) {
        return gameReadService.getGamePhase(code);
    }

    @GetMapping("/settings/{code}")
    public GameSettingsDTO getGameSettings(@PathVariable String code) {
        return gameReadService.getGameSettings(code);
    }
}
