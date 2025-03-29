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


    public Player joinRoom(String roomCode, Player player) {
        Room room = validateJoin(roomCode, player.getNickname());

        room.addPlayer(player);
        player.setColor(RandomColorUtil.assignColor(roomCode));
        return playerRepository.save(player);
    }

    public Room validateJoin(String roomCode, String nickname) {
        Room room = getRoom(roomCode);
        validateNicknameNotInRoom(room, nickname);

        return room;
    }

    private Room getRoom(String roomCode) {
        Room room = roomRepository.findByCode(roomCode);
        if (room == null) {
            throw new RoomNotFoundException(roomCode);
        }
        return room;
    }

    private void validateNicknameNotInRoom(Room room, String nickname) {
        Player playerInRoom = playerRepository.findByNicknameAndRoom(nickname, room);
        if (playerInRoom != null) {
            throw new NicknameAlreadyInRoomException(room.getCode(), nickname);
        }
    }
}
