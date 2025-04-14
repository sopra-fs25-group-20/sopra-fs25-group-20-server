package ch.uzh.ifi.hase.soprafs25.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerSessionManager {

    private static final Map<PlayerSessionKey, String> sessionMap = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(PlayerSessionManager.class);

    public static void addSession(String nickname, String roomCode, String sessionId) {
        PlayerSessionKey key = new PlayerSessionKey(nickname, roomCode);

        // If key is absent, it puts the returned value to the map
        sessionMap.computeIfAbsent(key, k -> {
            log.info("Adding session {} to {}", sessionId, nickname);
            return sessionId;
        });
    }

    public static String getSessionId(String nickname, String roomCode) {
        return sessionMap.get(new PlayerSessionKey(nickname, roomCode));
    }

    public static void removeSession(String nickname, String roomCode) {
        //sessionMap.remove(new PlayerSessionKey(nickname, roomCode));

        PlayerSessionKey key = new PlayerSessionKey(nickname, roomCode);
        if (sessionMap.remove(key) != null) {
            log.info("Removed session for {} in {}", nickname, roomCode);
        } else {
            log.warn("Tried to remove nonexistent session for {} in {}", nickname, roomCode);
        }
    }
}