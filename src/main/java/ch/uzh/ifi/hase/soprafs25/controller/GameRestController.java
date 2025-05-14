package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs25.model.*;
import ch.uzh.ifi.hase.soprafs25.service.GameReadService;
import ch.uzh.ifi.hase.soprafs25.service.PlayerConnectionService;
import ch.uzh.ifi.hase.soprafs25.service.VotingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GameRestController {

    private final PlayerConnectionService playerConnectionService;
    private final GameReadService gameReadService;
    private final VotingService votingService;

    public GameRestController(PlayerConnectionService playerConnectionService,
                              GameReadService gameReadService,
                              VotingService votingService) {
        this.playerConnectionService = playerConnectionService;
        this.gameReadService = gameReadService;
        this.votingService = votingService;
    }

    @GetMapping("/players/{code}")
    public List<PlayerUpdateDTO> getPlayerList(@PathVariable String code) {
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

    @GetMapping("/game/result/{code}")
    public GameResultDTO getResults(@PathVariable String code) {
        return gameReadService.getGameResult(code);
    }

    @GetMapping("/game/vote/target/{code}")
    public VoteStartDTO getVoteTarget(@PathVariable String code) {
        return votingService.getVoteTarget(code);
    }

    @GetMapping("/game/vote/state/{code}")
    public VoteStateDTO getVoteState(@PathVariable String code) {
        return votingService.getVoteState(code);
    }

    @GetMapping("/game/timer/{code}")
    public TimerDTO getTimer(@PathVariable String code, @RequestParam GamePhase phase) {
        return gameReadService.getTimer(code, phase);
    }
}
