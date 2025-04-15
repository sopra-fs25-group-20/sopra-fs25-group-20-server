package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.entity.Room;
import ch.uzh.ifi.hase.soprafs25.model.PlayerListUpdateDTO;
import ch.uzh.ifi.hase.soprafs25.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerConnectionService {

    private final PlayerRepository playerRepository;
    private final RoomRepository roomRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final AuthorizationService authorizationService;

    public PlayerConnectionService(PlayerRepository playerRepository,
                                   RoomRepository roomRepository,
                                   SimpMessagingTemplate messagingTemplate,
                                   AuthorizationService authorizationService) {
        this.playerRepository = playerRepository;
        this.roomRepository = roomRepository;
        this.messagingTemplate = messagingTemplate;
        this.authorizationService = authorizationService;
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

    public void broadcastPlayerList(String roomCode) {
        List<PlayerListUpdateDTO> playerList = getPlayerListDTO(roomCode);

        messagingTemplate.convertAndSend("/topic/players/" + roomCode, playerList);
    }

    public List<PlayerListUpdateDTO> getPlayerListDTO(String roomCode) {
        List<Player> players = getPlayers(roomCode);
        return players.stream()
                .map(p -> new PlayerListUpdateDTO(p.getNickname(), p.getColor()))
                .toList();
    }

    public void kickPlayer(String kickerNickname, String kickedNickname, String roomCode) {
        if (!authorizationService.isAdmin(roomCode, kickerNickname)){
            throw new IllegalStateException("Only admin can kick a player");
        }

        Room room = roomRepository.findByCode(roomCode);
        Player kickedPlayer = playerRepository.findByNicknameAndRoom(kickedNickname, room);
        if (kickedPlayer == null) {
            throw new IllegalStateException("Player with nickname " + kickedNickname + " not found");
        }
        playerRepository.delete(kickedPlayer);
        broadcastPlayerList(roomCode);
    }

    public List<Player> getPlayers(String roomCode) {
        Room room = roomRepository.findByCode(roomCode);
        if (room == null) {
            throw new IllegalStateException("Room not found: " + roomCode);
        }
        return room.getPlayers();
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
}
