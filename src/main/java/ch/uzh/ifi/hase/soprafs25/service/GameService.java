package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;
import ch.uzh.ifi.hase.soprafs25.entity.*;
import ch.uzh.ifi.hase.soprafs25.model.*;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs25.service.image.ImageService;
import ch.uzh.ifi.hase.soprafs25.session.GameSessionManager;
import ch.uzh.ifi.hase.soprafs25.session.VotingSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


@Service
public class GameService {

    private static final Logger log = LoggerFactory.getLogger(GameService.class);
    private final ImageService imageService;
    private final GameTimerService gameTimerService;
    private final SimpMessagingTemplate messagingTemplate;
    private final AuthorizationService authorizationService;
    private final RoomRepository roomRepository;

    public GameService(@Qualifier("mockImageService") ImageService imageService,
                       GameTimerService gameTimerService,
                       SimpMessagingTemplate messagingTemplate,
                       AuthorizationService authorizationService,
                       RoomRepository roomRepository) {
        this.imageService = imageService;
        this.gameTimerService = gameTimerService;
        this.messagingTemplate = messagingTemplate;
        this.authorizationService = authorizationService;
        this.roomRepository = roomRepository;
    }

    public void createGame(String roomCode) {
        if (GameSessionManager.isActive(roomCode)) {
            throw new IllegalStateException("Game already active for room: " + roomCode);
        }

        Game game = new Game(roomCode);
        GameSessionManager.addGameSession(game);
    }

    public void startRound(String roomCode, String nickname) {
        if (!authorizationService.isAdmin(roomCode, nickname)){
            throw new IllegalStateException("Only admin can start the round");
        }
        Game game = getGame(roomCode);

        advancePhase(roomCode, GamePhase.GAME);
        game.assignRoles(getNicknamesInRoom(roomCode));
        game.setHighlightedImageIndex(new Random().nextInt(game.getGameSettings().getImageCount()));
        game.setImages(loadOrFetchImages(game));

        scheduleRoundTimeout(roomCode);
    }

    public void advancePhase(String roomCode, GamePhase newPhase) {
        Game game = getGame(roomCode);
        GamePhase oldPhase = game.getPhase();
        game.setPhase(newPhase);

        if (newPhase == GamePhase.SUMMARY) {
            gameTimerService.cancel(roomCode + "-vote");
            gameTimerService.cancel(roomCode + "-round");
            clearImages(game);
        }
        if (oldPhase == GamePhase.VOTE && newPhase == GamePhase.GAME) {
            gameTimerService.cancel(roomCode + "-vote");
        }
        broadcastGamePhase(roomCode);
    }

    public void handleSpyGuess(String roomCode, String nickname, int guessIndex) {
        boolean spyGuessCorrect = checkSpyGuess(roomCode, nickname, guessIndex);
        if (spyGuessCorrect) {
            createGameResult(roomCode, PlayerRole.SPY);
        } else {
            createGameResult(roomCode, PlayerRole.INNOCENT);
        }

        advancePhase(roomCode, GamePhase.SUMMARY);
        gameTimerService.cancel(roomCode + "-round");
    }

    public GameSettingsDTO changeGameSettings(String roomCode, String nickname, GameSettingsDTO gameSettings) {
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
        broadcastGameSettings(roomCode);
        return gameSettings;
    }

    public void broadcastPersonalizedRole(String roomCode, String nickname) {
        PlayerRole playerRole = getPlayerRole(roomCode, nickname);
        PlayerRoleDTO dto = new PlayerRoleDTO(playerRole.name().toLowerCase());

        messagingTemplate.convertAndSendToUser(nickname + ":" + roomCode, "/queue/role/" + roomCode, dto);
    }

    public void broadcastPersonalizedImageIndex(String roomCode, String nickname) {
        int highlightedImageIndex = getHighlightedImageIndexForPlayer(roomCode, nickname);
        HighlightedImageIndexDTO dto = new HighlightedImageIndexDTO(highlightedImageIndex);

        messagingTemplate.convertAndSendToUser(nickname + ":" + roomCode, "/queue/highlighted/" + roomCode, dto);
    }

    public ResultDTO getGameResult(String roomCode) {
        Game game = getGame(roomCode);

        if (game.getPhase() != GamePhase.SUMMARY) {
            throw new IllegalStateException("Can only get the results in summary phase");
        }
        return new ResultDTO(
                game.getRoles(),
                game.getGameResult().getWinnerRole(),
                game.getHighlightedImageIndex()
        );
    }

    public GamePhaseDTO getGamePhase(String roomCode) {
        GamePhase gamePhase = getGame(roomCode).getPhase();
        return new GamePhaseDTO(gamePhase.name().toLowerCase());
    }

    public GameSettingsDTO getGameSettings(String roomCode) {
        GameSettings gameSettings = getGame(roomCode).getGameSettings();
        return new GameSettingsDTO(
                gameSettings.getVotingTimer(),
                gameSettings.getGameTimer(),
                gameSettings.getImageCount(),
                gameSettings.getImageRegion()
        );
    }

    public Game getGame(String roomCode) {
        return GameSessionManager.getGameSession(roomCode);
    }

    private void scheduleRoundTimeout(String roomCode) {
        int gameTimerSeconds = getGame(roomCode)
                .getGameSettings()
                .getGameTimer();

        gameTimerService.schedule(
                roomCode + "-round",
                () -> {
                    try {
                        if (GameSessionManager.isActive(roomCode)) {
                            log.info("Round timer expired for room: {}", roomCode);
                            createGameResult(roomCode, PlayerRole.SPY);
                            advancePhase(roomCode, GamePhase.SUMMARY);
                        }
                    } catch (Exception e) {
                        log.error("Error while ending round for room " + roomCode, e);
                    }
                },
                gameTimerSeconds
        );
    }

    private void broadcastGamePhase(String roomCode) {
        GamePhaseDTO gamePhaseDTO = getGamePhase(roomCode);
        messagingTemplate.convertAndSend("/topic/phase/" + roomCode, gamePhaseDTO);
    }

    private void broadcastGameSettings(String roomCode) {
        Game game = getGame(roomCode);
        GameSettings gameSettings = game.getGameSettings();

        GameSettingsDTO dto = new GameSettingsDTO(
                gameSettings.getVotingTimer(),
                gameSettings.getGameTimer(),
                gameSettings.getImageCount(),
                gameSettings.getImageRegion()
        );
        messagingTemplate.convertAndSendToUser(roomCode, "/topic/settings" + roomCode, dto);
    }

    private PlayerRole getPlayerRole(String roomCode, String nickname) {
        Game game = getGame(roomCode);
        return game.getRole(nickname);
    }

    private int getHighlightedImageIndexForPlayer(String roomCode, String nickname) {
        PlayerRole role = getPlayerRole(roomCode, nickname);
        return (role == PlayerRole.INNOCENT) ? getGame(roomCode).getHighlightedImageIndex() : -1;
    }

    private void createGameResult(String roomCode, PlayerRole winnerRole) {
        getGame(roomCode).getGameResult().setWinnerRole(winnerRole);
    }

    private boolean checkSpyGuess(String roomCode, String nickname, int guessIndex) {
        Game game = getGame(roomCode);
        if (game.getRole(nickname) != PlayerRole.SPY) {
            throw new IllegalStateException("Innocents can guess the image");
        }

        return game.getHighlightedImageIndex() == guessIndex;
    }

    private List<String> getNicknamesInRoom(String roomCode) {
        Room room = roomRepository.findByCode(roomCode);
        if (room == null) {
            throw new IllegalStateException("Room not found: " + roomCode);
        }

        return room.getPlayers().stream()
                .map(Player::getNickname)
                .collect(Collectors.toList());
    }

    private List<byte[]> loadOrFetchImages(Game game) {
        if (!game.getImages().isEmpty()) {
            return game.getImages();
        }

        List<byte[]> imageList = new ArrayList<>();
        for (int i = 0; i < game.getGameSettings().getImageCount(); i++) {
            byte[] img = imageService.fetchImageByLocation(game.getGameSettings().getImageRegion());
            imageList.add(img);
        }
        return imageList;
    }

    private void clearImages(Game game) {
        game.getImages().clear();
    }
}
