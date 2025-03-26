package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.entity.Room;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs25.util.RoomCodeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateRoomService {

    private final RoomRepository roomRepository;
    private final JoinRoomService joinRoomService;

    public CreateRoomService(RoomRepository roomRepository,
                             JoinRoomService joinRoomService) {
        this.roomRepository = roomRepository;
        this.joinRoomService = joinRoomService;
    }


    public Player createRoom(Player player) {
        Room createdRoom = new Room();
        createdRoom.setCode(RoomCodeUtil.generateRoomCode());
        createdRoom = roomRepository.save(createdRoom);

        Player savedPlayer = joinRoomService.joinRoom(createdRoom.getCode(), player);

        createdRoom.setAdminPlayerId(savedPlayer.getId());
        roomRepository.save(createdRoom);

        return savedPlayer;
    }
}
