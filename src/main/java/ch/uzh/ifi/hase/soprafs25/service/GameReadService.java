package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;
import ch.uzh.ifi.hase.soprafs25.entity.*;
import ch.uzh.ifi.hase.soprafs25.model.*;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs25.session.GameSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GameReadService {

    private static final Logger log = LoggerFactory.getLogger(GameReadService.class);
    private final RoomRepository roomRepository;
    private final GameTimerService gameTimerService;

    public GameReadService(RoomRepository roomRepository, GameTimerService gameTimerService) {
        this.roomRepository = roomRepository;
        this.gameTimerService = gameTimerService;
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

    public GameResultDTO getGameResult(String roomCode) {
        Game game = getGame(roomCode);
        if (game.getPhase() != GamePhase.SUMMARY) {
            throw new IllegalStateException("Can only get the results in summary phase");
        }

        GameResult result = game.getGameResult();
        if (result == null) {
            throw new IllegalStateException("Game result not available yet");
        }

        return new GameResultDTO(
                game.getRoles(),
                game.getColors(),
                game.getHighlightedImageIndex(),
                result
        );
    }

    public List<String> getNicknamesInRoom(String roomCode) {
        Room room = roomRepository.findByCode(roomCode);
        if (room == null) {
            throw new IllegalStateException("Room not found: " + roomCode);
        }

        return room.getPlayers().stream()
                .map(Player::getNickname)
                .toList();
    }

    public List<PlayerUpdateDTO> getPlayerUpdateList(String roomCode) {
        Room room = roomRepository.findByCode(roomCode);
        if (room == null) {
            throw new IllegalStateException("Room not found: " + roomCode);
        }

        Long adminId = room.getAdminPlayerId();
        List<Player> players = room.getPlayers();

        return players.stream()
                .map(p -> new PlayerUpdateDTO(
                        p.getNickname(),
                        p.getColor(),
                        p.getId().equals(adminId),
                        p.getUser()
                ))
                .toList();
    }

    public int getPlayerCount(String roomCode) {
        Room room = roomRepository.findByCode(roomCode);
        if (room == null) {
            throw new IllegalStateException("Room not found: " + roomCode);
        }
        return room.getPlayers().size();
    }

    public PlayerRole getPlayerRole(String roomCode, String nickname) {
        Game game = getGame(roomCode);
        return game.getRole(nickname);
    }

    public Integer getPersonalizedImageIndex(String roomCode, String nickname) {
        PlayerRole role = getGame(roomCode).getRole(nickname);
        if (role == null) {
            return null;
        }
        return (role == PlayerRole.INNOCENT)
                ? getGame(roomCode).getHighlightedImageIndex()
                : -1;
    }

    public TimerDTO getTimer(String roomCode, GamePhase phase) {
        String timerId = roomCode + "_" + phase.name().toLowerCase();
        if (!gameTimerService.isTimerActive(timerId)) {
            log.warn("Timer ID '{}' is not active.", timerId);
            return new TimerDTO(null);
        }

        Optional<Long> remainingSecondsOpt = gameTimerService.getRemainingSeconds(timerId);
        Long remainingSecondsLong = remainingSecondsOpt.orElseGet(() -> {
            log.warn("Timer ID '{}' returned no remaining seconds", timerId);
            return 0L;
        });
        int remainingSeconds = remainingSecondsLong.intValue();
        return new TimerDTO(remainingSeconds);
    }

    public List<Player> getPlayersInRoom(String roomCode) {
        Room room = roomRepository.findByCode(roomCode);
        if (room == null) {
            throw new IllegalStateException("Room not found: " + roomCode);
        }
        return room.getPlayers();
    }

    public Map<String, String> getNicknameAndColor(String roomCode) {
        return getPlayersInRoom(roomCode).stream()
                .collect(Collectors.toMap(
                        Player::getNickname,
                        Player::getColor
                ));
    }

    private Game getGame(String roomCode) {
        return GameSessionManager.getGameSession(roomCode);
    }
}
