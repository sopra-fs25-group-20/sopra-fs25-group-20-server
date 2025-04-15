package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;
import ch.uzh.ifi.hase.soprafs25.entity.Game;
import ch.uzh.ifi.hase.soprafs25.model.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs25.session.GameSessionManager;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class GameService {

    private final AuthorizationService authorizationService;
    private final GameReadService gameReadService;
    private final GameBroadcastService gameBroadcastService;

    public GameService(AuthorizationService authorizationService,
                       GameReadService gameReadService,
                       GameBroadcastService gameBroadcastService) {
        this.authorizationService = authorizationService;
        this.gameReadService = gameReadService;
        this.gameBroadcastService = gameBroadcastService;
    }

    public void startRound(String roomCode, String nickname) {
        if (!authorizationService.isAdmin(roomCode, nickname)){
            throw new IllegalStateException("Only admin can start the round");
        }
        Game game = getGame(roomCode);

        advancePhase(roomCode, GamePhase.GAME);
        game.assignRoles(gameReadService.getNicknamesInRoom(roomCode));
        game.setHighlightedImageIndex(new Random().nextInt(game.getGameSettings().getImageCount()));
        prepareImagesForRound();
    }

    public void advancePhase(String roomCode, GamePhase newPhase) {
        Game game = getGame(roomCode);
        GamePhase oldPhase = game.getPhase();
        game.setPhase(newPhase);

        if (oldPhase == GamePhase.SUMMARY && newPhase == GamePhase.GAME) {
            game.clearImages();
        }
        gameBroadcastService.broadcastGamePhase(roomCode);
    }

    public void handleSpyGuess(String roomCode, String nickname, int spyGuessIndex) {
        Game game = getGame(roomCode);
        boolean spyGuessCorrect = checkSpyGuess(roomCode, nickname, spyGuessIndex);

        if (spyGuessCorrect) {
            game.setGameResult(spyGuessIndex, null, PlayerRole.SPY);
        } else {
            game.setGameResult(spyGuessIndex, null, PlayerRole.INNOCENT);
        }

        advancePhase(roomCode, GamePhase.SUMMARY);
    }

    public void changeGameSettings(String roomCode, String nickname, GameSettingsDTO gameSettings) {
        if (!authorizationService.isAdmin(roomCode, nickname)){
            throw new IllegalStateException("Only admin can change the settings");
        }
        Game game = getGame(roomCode);
        game.setGameSettings(
                gameSettings.getVotingTimer(),
                gameSettings.getGameTimer(),
                gameSettings.getImageCount(),
                gameSettings.getImageRegion()
        );
        gameBroadcastService.broadcastGameSettings(roomCode);
    }

    private boolean checkSpyGuess(String roomCode, String nickname, int spyGuessIndex) {
        Game game = getGame(roomCode);
        if (game.getRole(nickname) != PlayerRole.SPY) {
            throw new IllegalStateException("Innocents can't guess the image");
        }

        return game.getHighlightedImageIndex() == spyGuessIndex;
    }

    private void prepareImagesForRound() {
        // ToDo: Assign images to the game object if missing, return the existing otherwise
    }

    private Game getGame(String roomCode) {
        return GameSessionManager.getGameSession(roomCode);
    }
}
