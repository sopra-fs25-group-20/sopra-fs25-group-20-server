package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.model.*;
import ch.uzh.ifi.hase.soprafs25.service.GameService;
import ch.uzh.ifi.hase.soprafs25.service.PlayerConnectionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GameRestController {


    private final PlayerConnectionService playerConnectionService;
    private final GameService gameService;

    public GameRestController(PlayerConnectionService playerConnectionService,
                              GameService gameService) {
        this.playerConnectionService = playerConnectionService;
        this.gameService = gameService;
    }

    @GetMapping("/players/{code}")
    public List<PlayerListUpdateDTO> getPlayerList(@PathVariable String code) {
        return playerConnectionService.getPlayerListDTO(code);
    }

    @GetMapping("/phase/{code}")
    public GamePhaseDTO getGamePhase(@PathVariable String code) {
        return gameService.getGamePhase(code);
    }

    @GetMapping("/settings/{code}")
    public GameSettingsDTO getGameSettings(@PathVariable String code) {
        return gameService.getGameSettings(code);
    }

    @GetMapping("/game/result/{code}")
    public ResultDTO getResults(@PathVariable String code) {
        return gameService.getGameResult(code);
    }

    @GetMapping("/game/vote/{code}")
    public VoteStartDTO getVoteTarget(@PathVariable String code) {
        return gameService.getVoteTarget(code);
    }
}
