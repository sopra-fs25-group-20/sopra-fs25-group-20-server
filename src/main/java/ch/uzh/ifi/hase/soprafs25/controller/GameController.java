package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.model.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs25.model.ImageIndexDTO;
import ch.uzh.ifi.hase.soprafs25.service.GameService;
import ch.uzh.ifi.hase.soprafs25.util.SessionUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

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
    public void spyGuess(@Payload ImageIndexDTO imageIndexDTO, Message<?> socketMessage) {
        String code = SessionUtil.getCode(socketMessage);
        String nickname = SessionUtil.getNickname(socketMessage);
        int spyGuessIndex = imageIndexDTO.getIndex();

        gameService.handleSpyGuess(code, nickname, spyGuessIndex);
    }

    @MessageMapping("/game/settings")
    public void gameSettings(@Payload GameSettingsDTO gameSettings, Message<?> socketMessage) {
        String code = SessionUtil.getCode(socketMessage);
        String nickname = SessionUtil.getNickname(socketMessage);

        gameService.changeGameSettings(code, nickname, gameSettings);
    }
}
