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

    public PlayerConnectionService(PlayerRepository playerRepository,
                                   RoomRepository roomRepository,
                                   SimpMessagingTemplate messagingTemplate) {
        this.playerRepository = playerRepository;
        this.roomRepository = roomRepository;
        this.messagingTemplate = messagingTemplate;
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
        List<Player> players  = getPlayers(roomCode);

        List<PlayerListUpdateDTO> playerList = players.stream()
                .map(p -> new PlayerListUpdateDTO(p.getNickname(), p.getColor()))
                .toList();

        messagingTemplate.convertAndSend("/topic/players/" + roomCode, playerList);
    }

    private Player getPlayer(String nickname, String roomCode) {
        Room room = roomRepository.findByCode(roomCode);
        return playerRepository.findByNicknameAndRoom(nickname, room);
    }

    private List<Player> getPlayers(String nickname) {
        Room room = roomRepository.findByCode(nickname);
        return room.getPlayers();
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
