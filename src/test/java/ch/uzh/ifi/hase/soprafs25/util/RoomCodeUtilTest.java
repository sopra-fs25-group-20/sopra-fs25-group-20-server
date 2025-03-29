package ch.uzh.ifi.hase.soprafs25.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class RoomCodeUtilTest {

    @Test
    public void testGenerateRoomCode() {
        String roomCode = RoomCodeUtil.generateRoomCode();
        assertEquals(6, roomCode.length());
        assertTrue(roomCode.matches("[A-Z0-9]+"));
    }
}