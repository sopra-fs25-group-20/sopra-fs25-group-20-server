package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;
import ch.uzh.ifi.hase.soprafs25.entity.Game;
import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.entity.Room;
import ch.uzh.ifi.hase.soprafs25.model.GamePhaseDTO;
import ch.uzh.ifi.hase.soprafs25.model.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs25.model.ResultDTO;
import ch.uzh.ifi.hase.soprafs25.model.RoundStartDTO;
import ch.uzh.ifi.hase.soprafs25.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs25.service.image.ImageService;
import ch.uzh.ifi.hase.soprafs25.session.GameSessionManager;
import ch.uzh.ifi.hase.soprafs25.session.PlayerSessionManager;
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
    private final RoomRepository roomRepository;
    private final PlayerRepository playerRepository;
    private final ImageService imageService;
    private final GameTimerService gameTimerService;
    private final SimpMessagingTemplate messagingTemplate;

    public GameService(RoomRepository roomRepository,
                       PlayerRepository playerRepository,
                       @Qualifier("mockImageService") ImageService imageService,
                       GameTimerService gameTimerService,
                       SimpMessagingTemplate messagingTemplate) {
        this.roomRepository = roomRepository;
        this.playerRepository = playerRepository;
        this.imageService = imageService;
        this.gameTimerService = gameTimerService;
        this.messagingTemplate = messagingTemplate;
    }

    public void createGame(String roomCode) {
        if (GameSessionManager.isActive(roomCode)) {
            throw new IllegalStateException("Game already active for room: " + roomCode);
        }

        Game game = new Game(roomCode);
        GameSessionManager.addGameSession(game);
    }

    public void startRound(String roomCode, String nickname) {
        if (!isAdmin(roomCode, nickname)){
            throw new IllegalStateException("Only admin can start the round");
        }
        Game game = getGame(roomCode);

        advancePhase(roomCode, GamePhase.GAME);
        game.assignRoles(getNicknamesInRoom(roomCode));
        game.setHighlightedImageIndex(new Random().nextInt(game.getGameSettings().getImageCount()));
        game.setImages(getImages(game));

        scheduleRoundTimeout(roomCode);

        sendRoundStartMessages(roomCode);
    }

    public void advancePhase(String roomCode, GamePhase newPhase) {
        Game game = getGame(roomCode);
        GamePhase oldPhase = game.getPhase();
        game.setPhase(newPhase);

        if (newPhase == GamePhase.SUMMARY) {
            gameTimerService.cancel(roomCode + "-vote");
            gameTimerService.cancel(roomCode + "-round");
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
        if (!isAdmin(roomCode, nickname)){
            throw new IllegalStateException("Only admin can change the settings");
        }
        Game game = getGame(roomCode);
        game.setGameSettings(
                gameSettings.getVotingTimer(),
                gameSettings.getGameTimer(),
                gameSettings.getImageRegion(),
                gameSettings.getImageCount()
        );
        return gameSettings;
    }

    public void kickPlayer(String kickerNickname, String kickedNickname, String roomCode) {
        if (!isAdmin(roomCode, kickerNickname)){
            throw new IllegalStateException("Only admin can kick a player");
        }

        Room room = roomRepository.findByCode(roomCode);
        Player kickedPlayer = playerRepository.findByNicknameAndRoom(kickedNickname, room);
        if (kickedPlayer == null) {
            throw new IllegalStateException("Player with nickname " + kickerNickname + " not found");
        }
        room.removePlayer(kickedPlayer);
    }

    public ResultDTO getGameResult(String roomCode) {
        Game game = getGame(roomCode);

        return new ResultDTO(
                game.getRoles(),
                game.getGameResult().getWinnerRole(),
                game.getHighlightedImageIndex()
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

    private void sendRoundStartMessages(String roomCode) {
        Game game = getGame(roomCode);
        List<String> nicknames = getNicknamesInRoom(roomCode);
        int highlightedIndex = game.getHighlightedImageIndex();

        for (String nickname : nicknames) {
            PlayerRole role = game.getRole(nickname);
            String sessionId = PlayerSessionManager.getSessionId(nickname, roomCode);

            if (sessionId == null) {
                log.warn("No sessionId found for {} in {}", nickname, roomCode);
                continue;
            }

            RoundStartDTO dto = new RoundStartDTO();
            dto.setRole(role);
            dto.setHighlightedImageIndex(role == PlayerRole.SPY ? -1 : highlightedIndex);

            messagingTemplate.convertAndSendToUser(
                    sessionId,
                    "/queue/round/start",
                    dto
            );
        }
    }

    private void broadcastGamePhase(String roomCode) {
        GamePhase gamePhase = getGame(roomCode).getPhase();
        messagingTemplate.convertAndSend(
                "/topic/phase/" + roomCode,
                new GamePhaseDTO(gamePhase.name().toLowerCase())
        );
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

    private List<byte[]> getImages(Game game) {
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

    private boolean isAdmin(String roomCode, String nickname) {
        Room room = roomRepository.findByCode(roomCode);
        if (room == null) {
            throw new IllegalStateException("Room not found: " + roomCode);
        }

        Player player = playerRepository.findByNicknameAndRoom(nickname, room);
        if (player == null) {
            throw new IllegalStateException("Player not found in room");
        }

        if (!room.getAdminPlayerId().equals(player.getId())) {
            log.warn("Only admin can do this action");
            return false;
        }
        return true;
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
}
