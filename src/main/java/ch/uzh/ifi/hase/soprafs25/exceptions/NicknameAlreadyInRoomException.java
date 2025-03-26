package ch.uzh.ifi.hase.soprafs25.exceptions;

public class NicknameAlreadyInRoomException extends RuntimeException {
    public NicknameAlreadyInRoomException(String roomCode, String nickname) {
        super(ErrorMessages.NICKNAME_ALREADY_IN_ROOM.format(nickname, roomCode));
    }
}
