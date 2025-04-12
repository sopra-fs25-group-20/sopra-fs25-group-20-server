package ch.uzh.ifi.hase.soprafs25.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class GameSessionManager {
    
    private static final Map<String, Game> gameSessions = new ConcurrentHashMap<>();

    private GameSessionManager() {}

    public static void addGameSession(Game game) {
        if (game == null || game.getRoomCode() == null) {
            throw new IllegalStateException("Game or roomCode must not be null");
        }
        gameSessions.put(game.getRoomCode(), game);
    }

    public static Game getGameSession(String roomCode) {
        Game game = gameSessions.get(roomCode);
        if (game == null) {
            throw new IllegalStateException("No active game session found for room: " + roomCode);
        }
        return game;
    }

    public static void removeGameSession(String roomCode) {
        if (!gameSessions.containsKey(roomCode)) {
            throw new IllegalStateException("Cannot remove: no game session found for room: " + roomCode);
        }
        gameSessions.remove(roomCode);
    }

    public static boolean isActive(String roomCode) {
        return gameSessions.containsKey(roomCode);
    }
}
