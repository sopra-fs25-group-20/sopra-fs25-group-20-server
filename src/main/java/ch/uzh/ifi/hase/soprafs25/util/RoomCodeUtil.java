package ch.uzh.ifi.hase.soprafs25.util;

import java.util.UUID;

public class RoomCodeUtil {

    public static String generateRoomCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
