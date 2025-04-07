package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.entity.Room;
import ch.uzh.ifi.hase.soprafs25.exceptions.NicknameAlreadyInRoomException;
import ch.uzh.ifi.hase.soprafs25.exceptions.RoomNotFoundException;
import ch.uzh.ifi.hase.soprafs25.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs25.util.RandomColorUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JoinRoomService {

    private final PlayerRepository playerRepository;
    private final RoomRepository roomRepository;

    public JoinRoomService(PlayerRepository playerRepository,
                           RoomRepository roomRepository) {
        this.playerRepository = playerRepository;
        this.roomRepository = roomRepository;
    }


    public Player joinRoom(String roomCode, Player playerInput) {
        Room room = getRoom(roomCode);
        Player existingPlayer = playerRepository.findByNicknameAndRoom(playerInput.getNickname(), room);

        if (existingPlayer != null) {
            if (existingPlayer.isConnected()) {
                throw new NicknameAlreadyInRoomException(roomCode, playerInput.getNickname());
            }
            return reconnect(existingPlayer);
        }

        return createNewPlayer(room, playerInput);
    }

    public void validateJoin(String roomCode, String nickname) {
        Room room = getRoom(roomCode);
        validateNicknameNotActive(room, nickname);
    }

    private Player createNewPlayer(Room room, Player playerInput) {
        playerInput.setRoom(room);
        playerInput.setColor(RandomColorUtil.assignColor(room.getCode()));
        playerInput.setConnected(false);
        return playerRepository.save(playerInput);
    }

    private Player reconnect(Player player) {
        return playerRepository.save(player);
    }

    private Room getRoom(String roomCode) {
        Room room = roomRepository.findByCode(roomCode);
        if (room == null) {
            throw new RoomNotFoundException(roomCode);
        }
        return room;
    }

    private void validateNicknameNotActive(Room room, String nickname) {
        Player player = playerRepository.findByNicknameAndRoom(nickname, room);

        if (player != null && player.isConnected()) {
            throw new NicknameAlreadyInRoomException(room.getCode(), nickname);
        }
    }
}
