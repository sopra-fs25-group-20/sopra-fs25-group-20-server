package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;
import ch.uzh.ifi.hase.soprafs25.entity.*;
import ch.uzh.ifi.hase.soprafs25.model.GamePhaseDTO;
import ch.uzh.ifi.hase.soprafs25.model.GameResultDTO;
import ch.uzh.ifi.hase.soprafs25.model.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs25.model.PlayerUpdateDTO;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs25.session.GameSessionManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameReadService {

    private final RoomRepository roomRepository;

    public GameReadService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
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

        List<Player> players = room.getPlayers();
        return players.stream()
                .map(p -> new PlayerUpdateDTO(p.getNickname(), p.getColor()))
                .toList();
    }

    public PlayerRole getPlayerRole(String roomCode, String nickname) {
        Game game = getGame(roomCode);
        return game.getRole(nickname);
    }

    public int getPersonalizedImageIndex(String roomCode, String nickname) {
        PlayerRole role = getGame(roomCode).getRole(nickname);
        return (role == PlayerRole.INNOCENT)
                ? getGame(roomCode).getHighlightedImageIndex()
                : -1;
    }

    private Game getGame(String roomCode) {
        return GameSessionManager.getGameSession(roomCode);
    }
}
