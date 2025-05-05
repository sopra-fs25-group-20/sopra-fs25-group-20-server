package ch.uzh.ifi.hase.soprafs25.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NicknameAlreadyInRoomExceptionTest {

    @Test
    @DisplayName("should format NICKNAME_ALREADY_IN_ROOM with nickname and roomCode")
    void shouldFormatNicknameAlreadyInRoomMessage() {
        String roomCode = "XYZ123";
        String nickname = "delta";
        NicknameAlreadyInRoomException ex = new NicknameAlreadyInRoomException(roomCode, nickname);

        String expected = ErrorMessages.NICKNAME_ALREADY_IN_ROOM.format(nickname, roomCode); // :contentReference[oaicite:12]{index=12}:contentReference[oaicite:13]{index=13}
        assertEquals(expected, ex.getMessage());
    }
}
