package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.model.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs25.model.ResultDTO;
import ch.uzh.ifi.hase.soprafs25.service.GameService;
import ch.uzh.ifi.hase.soprafs25.util.SessionUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
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

        gameService.kickPlayer(kickerNickname, kickedNickname, code);
    }

    @GetMapping("/game/result/{roomCode}")
    public ResultDTO getResults(@PathVariable String roomCode) {
        return gameService.getGameResult(roomCode);
    }
}
