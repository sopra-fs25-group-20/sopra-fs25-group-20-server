package ch.uzh.ifi.hase.soprafs25.exceptions;

public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException(String roomCode) {
        super(ErrorMessages.ROOM_NOT_FOUND.format(roomCode));
    }
}
