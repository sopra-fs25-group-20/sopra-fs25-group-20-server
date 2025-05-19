package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs25.entity.Game;
import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.entity.Room;
import ch.uzh.ifi.hase.soprafs25.model.PlayerUpdateDTO;
import ch.uzh.ifi.hase.soprafs25.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs25.session.GameSessionManager;
import ch.uzh.ifi.hase.soprafs25.util.RandomColorUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerConnectionService {

    private final PlayerRepository playerRepository;
    private final RoomRepository roomRepository;
    private final AuthorizationService authorizationService;
    private final GameBroadcastService gameBroadcastService;

    public PlayerConnectionService(PlayerRepository playerRepository,
                                   RoomRepository roomRepository,
                                   AuthorizationService authorizationService,
                                   GameBroadcastService gameBroadcastService) {
        this.playerRepository = playerRepository;
        this.roomRepository = roomRepository;
        this.authorizationService = authorizationService;
        this.gameBroadcastService = gameBroadcastService;
    }

    public void markConnected(String nickname, String roomCode) {
        updateConnection(nickname, roomCode, true);
    }

    public void markDisconnected(String nickname, String roomCode) {
        updateConnection(nickname, roomCode, false);
    }

    public boolean isOnline(String nickname, String roomCode) {
        Player player = getPlayer(nickname, roomCode);
        if (player == null) {
            return false;
        }
        return player.isConnected();
    }

    public List<PlayerUpdateDTO> getPlayerListDTO(String roomCode) {
        Room room = roomRepository.findByCode(roomCode);
        if (room == null) {
            throw new IllegalStateException("Room not found: " + roomCode);
        }

        Long adminId = room.getAdminPlayerId();
        List<Player> players = getPlayers(roomCode);
        return players.stream()
                .map(p -> new PlayerUpdateDTO(
                        p.getNickname(),
                        p.getColor(),
                        p.getId().equals(adminId),
                        p.getUser()))
                .toList();
    }

    public void kickPlayer(String kickerNickname, String kickedNickname, String roomCode) {
        if (!authorizationService.isAdmin(roomCode, kickerNickname)){
            throw new IllegalStateException("Only admin can kick a player");
        }

        removePlayer(kickedNickname, roomCode);
    }

    public void removePlayer(String kickedNickname, String roomCode) {
        Room room = roomRepository.findByCode(roomCode);
        Player kickedPlayer = playerRepository.findByNicknameAndRoom(kickedNickname, room);
        if (kickedPlayer == null) {
            throw new IllegalStateException("Player with nickname " + kickedNickname + " not found");
        }
        RandomColorUtil.releaseColor(roomCode, kickedPlayer.getColor());
        playerRepository.delete(kickedPlayer);
        gameBroadcastService.broadcastPlayerList(roomCode);
    }

    public List<Player> getPlayers(String roomCode) {
        Room room = roomRepository.findByCode(roomCode);
        if (room == null) {
            throw new IllegalStateException("Room not found: " + roomCode);
        }
        return room.getPlayers();
    }

    public void handleDisconnection(String nickname, String roomCode) {
        GamePhase gamePhase = getGame(roomCode).getPhase();
        if (gamePhase == GamePhase.LOBBY || gamePhase == GamePhase.SUMMARY) {
            removePlayer(nickname, roomCode);
        } else {
            markDisconnected(nickname, roomCode);
        }
    }

    private Player getPlayer(String nickname, String roomCode) {
        Room room = roomRepository.findByCode(roomCode);
        return playerRepository.findByNicknameAndRoom(nickname, room);
    }

    private void updateConnection(String nickname, String roomCode, boolean isConnected) {
        Room room = roomRepository.findByCode(roomCode);
        Player player = playerRepository.findByNicknameAndRoom(nickname, room);
        if (player != null && player.isConnected() != isConnected) {
            player.setConnected(isConnected);
            playerRepository.save(player);
        }
    }

    private Game getGame(String roomCode) {
        return GameSessionManager.getGameSession(roomCode);
    }
}
