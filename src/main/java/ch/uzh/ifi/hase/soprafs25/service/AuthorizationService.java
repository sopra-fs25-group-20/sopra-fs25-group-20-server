package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.entity.Room;
import ch.uzh.ifi.hase.soprafs25.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    private final RoomRepository roomRepository;
    private final PlayerRepository playerRepository;
    private final Logger log = LoggerFactory.getLogger(AuthorizationService.class);

    public AuthorizationService(RoomRepository roomRepository, PlayerRepository playerRepository) {
        this.roomRepository = roomRepository;
        this.playerRepository = playerRepository;
    }

    public boolean isAdmin(String roomCode, String nickname) {
        Room room = roomRepository.findByCode(roomCode);
        if (room == null) {
            throw new IllegalStateException("Room not found: " + roomCode);
        }

        Player player = playerRepository.findByNicknameAndRoom(nickname, room);
        if (player == null) {
            throw new IllegalStateException("Player '" + nickname + "' not found in room '" + roomCode + "'");
        }

        if (!room.getAdminPlayerId().equals(player.getId())) {
            log.warn("Only admin can do this action");
            return false;
        }
        return true;
    }
}
