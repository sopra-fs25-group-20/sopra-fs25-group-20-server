package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;
import ch.uzh.ifi.hase.soprafs25.entity.Game;
import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.model.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs25.service.image.ImageService;
import ch.uzh.ifi.hase.soprafs25.session.GameSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
public class GameService {

    private static final Random RANDOM = new Random(); // NOSONAR
    private static final Logger log = LoggerFactory.getLogger(GameService.class);
    private static final String GAME_SUFFIX = "_game";
    private static final String VOTE_SUFFIX = "_vote";

    private final AuthorizationService authorizationService;
    private final GameReadService gameReadService;
    private final GameBroadcastService gameBroadcastService;
    private final ImageService imageService;
    private final UserService userService;
    private final GameTimerService gameTimerService;

    public GameService(AuthorizationService authorizationService,
                       GameReadService gameReadService,
                       GameBroadcastService gameBroadcastService,
                       @Qualifier("googleImageService") ImageService mockImageService,
                       UserService userService,
                       GameTimerService gameTimerService) {
        this.authorizationService = authorizationService;
        this.gameReadService = gameReadService;
        this.gameBroadcastService = gameBroadcastService;
        this.imageService = mockImageService;
        this.userService = userService;
        this.gameTimerService = gameTimerService;
    }

    public void startRound(String roomCode, String nickname) {
        if (!authorizationService.isAdmin(roomCode, nickname)){
            throw new IllegalStateException("Only admin can start the round");
        }
        Game game = getGame(roomCode);

        prepareImagesForRound(game);
        game.assignRoles(gameReadService.getNicknamesInRoom(roomCode));
        game.setHighlightedImageIndex(RANDOM.nextInt(game.getGameSettings().getImageCount()));
        advancePhase(roomCode, GamePhase.GAME);

        Runnable taskForRoundTimeOut = () -> handleRoundTimeOut(roomCode);
        gameTimerService.scheduleTask(roomCode + GAME_SUFFIX, game.getGameSettings().getGameTimer(), taskForRoundTimeOut);
    }

    public void createGame(String roomCode) {
        if (GameSessionManager.isActive(roomCode)) {
            throw new IllegalStateException("Game already active for room: " + roomCode);
        }

        Game game = new Game(roomCode);
        GameSessionManager.addGameSession(game);
    }

    public void advancePhase(String roomCode, GamePhase newPhase) {
        Game game = getGame(roomCode);
        if (newPhase == GamePhase.SUMMARY) {
            if (gameTimerService.isTimerActive(roomCode + GAME_SUFFIX)) {
                gameTimerService.cancelTask(roomCode + GAME_SUFFIX, "Round ended early");
            }
            if (gameTimerService.isTimerActive(roomCode + VOTE_SUFFIX)) {
                gameTimerService.cancelTask(roomCode + VOTE_SUFFIX, "Round ended early");
            }
            updatePlayerStatsInRoom(roomCode);
        }
        game.setPhase(newPhase);

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

    public void broadcastPersonalizedRole(String roomCode, String nickname) {
        gameBroadcastService.broadcastPersonalizedRole(roomCode, nickname);
    }

    public void broadcastPersonalizedImageIndex(String roomCode, String nickname) {
        gameBroadcastService.broadcastPersonalizedImageIndex(roomCode, nickname);
    }

    private void updatePlayerStatsInRoom(String roomCode) {
        Game game = getGame(roomCode);

        List<Player> playersInRoom = gameReadService.getPlayersInRoom(roomCode);
        log.info("Players in the room '{}'", playersInRoom);
        List<Player> playersWithAccount = playersInRoom.stream()
                .filter(player -> player.getUser() != null)
                .toList();
        log.info("Players with account: {}", playersWithAccount);
        boolean hasWon;
        for (Player player : playersWithAccount) {
            hasWon = game.getRole(player.getNickname()) == game.getGameResult().getWinnerRole();
            log.info("Player '{}' has won: {}", player.getNickname(), hasWon);
            userService.updateUserStatsAfterGame(player.getUser(), hasWon);
        }
    }

    private void handleRoundTimeOut(String roomCode) {
        Game game = getGame(roomCode);
        game.setGameResult(null, null, PlayerRole.SPY);
        advancePhase(roomCode, GamePhase.SUMMARY);
    }

    private boolean checkSpyGuess(String roomCode, String nickname, int spyGuessIndex) {
        Game game = getGame(roomCode);
        if (game.getRole(nickname) != PlayerRole.SPY) {
            throw new IllegalStateException("Innocents can't guess the image");
        }

        return game.getHighlightedImageIndex() == spyGuessIndex;
    }

    private void prepareImagesForRound(Game game) {
        int count = game.getGameSettings().getImageCount();
        String region = game.getGameSettings().getImageRegion();

        List<CompletableFuture<byte[]>> futures = imageService.fetchImagesByLocationAsync(region, count);

        List<byte[]> imageList = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        game.setImages(imageList);
    }

    private Game getGame(String roomCode) {
        return GameSessionManager.getGameSession(roomCode);
    }
}
