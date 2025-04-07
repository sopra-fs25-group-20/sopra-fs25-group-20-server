package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.entity.Room;
import ch.uzh.ifi.hase.soprafs25.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import org.springframework.stereotype.Service;

@Service
public class PlayerConnectionService {

    private final PlayerRepository playerRepository;
    private final RoomRepository roomRepository;

    public PlayerConnectionService(PlayerRepository playerRepository,
                                   RoomRepository roomRepository) {
        this.playerRepository = playerRepository;
        this.roomRepository = roomRepository;
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
