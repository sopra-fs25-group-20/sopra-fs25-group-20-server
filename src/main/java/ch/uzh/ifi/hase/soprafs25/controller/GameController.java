package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.model.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs25.model.ImageIndexDTO;
import ch.uzh.ifi.hase.soprafs25.service.GameService;
import ch.uzh.ifi.hase.soprafs25.service.PlayerConnectionService;
import ch.uzh.ifi.hase.soprafs25.util.SessionUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

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

    @MessageMapping("/player/kick")
    public void kickPlayer(@Payload String kickedNickname, Message<?> socketMessage) {
        String code = SessionUtil.getCode(socketMessage);
        String kickerNickname = SessionUtil.getNickname(socketMessage);

        playerConnectionService.kickPlayer(kickerNickname, kickedNickname, code);
    }

    @MessageMapping("/role")
    public void getPlayerRole(Message<?> socketMessage) {
        String code = SessionUtil.getCode(socketMessage);
        String nickname = SessionUtil.getNickname(socketMessage);

        gameService.broadcastPersonalizedRole(code, nickname);
    }

    @MessageMapping("/highlighted")
    public void getImageIndex(Message<?> socketMessage) {
        String code = SessionUtil.getCode(socketMessage);
        String nickname = SessionUtil.getNickname(socketMessage);

        gameService.broadcastPersonalizedImageIndex(code, nickname);
    }
}
