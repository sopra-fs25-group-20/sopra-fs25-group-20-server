package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.model.GamePhaseDTO;
import ch.uzh.ifi.hase.soprafs25.model.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs25.model.PlayerListUpdateDTO;
import ch.uzh.ifi.hase.soprafs25.model.ResultDTO;
import ch.uzh.ifi.hase.soprafs25.service.GameService;
import ch.uzh.ifi.hase.soprafs25.service.PlayerConnectionService;
import ch.uzh.ifi.hase.soprafs25.util.SessionUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@Controller
public class GameController {

    private final GameService gameService;
    private final PlayerConnectionService playerConnectionService;

    public GameController(GameService gameService,
                          PlayerConnectionService playerConnectionService) {
        this.gameService = gameService;
        this.playerConnectionService = playerConnectionService;
    }

    @MessageMapping("/game/start")
    public void startGame(Message<?> socketMessage) {
        String code = SessionUtil.getCode(socketMessage);
        String nickname = SessionUtil.getNickname(socketMessage);

        gameService.startRound(code, nickname);
    }

    @MessageMapping("/game/guess")
    public void spyGuess(@Payload int guessIndex, Message<?> socketMessage) {
        String code = SessionUtil.getCode(socketMessage);
        String nickname = SessionUtil.getNickname(socketMessage);

        gameService.handleSpyGuess(code, nickname, guessIndex);
    }

    @MessageMapping("/settings")
    public GameSettingsDTO gameSettings(@Payload GameSettingsDTO gameSettings, Message<?> socketMessage) {
        String code = SessionUtil.getCode(socketMessage);
        String nickname = SessionUtil.getNickname(socketMessage);

        return gameService.changeGameSettings(code, nickname, gameSettings);
    }

    @MessageMapping("/player/kick")
    public void kickPlayer(@Payload String kickedNickname, Message<?> socketMessage) {
        String code = SessionUtil.getCode(socketMessage);
        String kickerNickname = SessionUtil.getNickname(socketMessage);

        playerConnectionService.kickPlayer(kickerNickname, kickedNickname, code);
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
}
