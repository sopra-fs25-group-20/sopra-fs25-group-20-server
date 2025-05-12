package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.entity.Room;
import ch.uzh.ifi.hase.soprafs25.entity.User;
import ch.uzh.ifi.hase.soprafs25.exceptions.UserNotAuthenticatedException;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs25.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs25.util.RoomCodeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateRoomService {

    private final RoomRepository roomRepository;
    private final JoinRoomService joinRoomService;
    private final GameService gameService;
    private final UserRepository userRepository;

    public CreateRoomService(RoomRepository roomRepository,
                             JoinRoomService joinRoomService,
                             GameService gameService,
                             UserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.joinRoomService = joinRoomService;
        this.gameService = gameService;
        this.userRepository = userRepository;
    }


    public Player createRoom(Player player, String tokenHeader) {
        Room createdRoom = new Room();
        createdRoom.setCode(RoomCodeUtil.generateRoomCode());
        createdRoom = roomRepository.save(createdRoom);

        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);
            User user = userRepository.findByToken(token);
            if (user == null) {
                throw new UserNotAuthenticatedException("Invalid token. Please log in again.");
            }
            player.setUser(user);
        }

        Player savedPlayer = joinRoomService.joinRoom(createdRoom.getCode(), player);

        createdRoom.setAdminPlayerId(savedPlayer.getId());
        roomRepository.save(createdRoom);

        gameService.createGame(createdRoom.getCode());
        return savedPlayer;
    }
}
