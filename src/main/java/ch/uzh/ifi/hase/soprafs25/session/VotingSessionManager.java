package ch.uzh.ifi.hase.soprafs25.session;

import ch.uzh.ifi.hase.soprafs25.entity.VotingSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class VotingSessionManager {
    
    private static final Map<String, VotingSession> sessions = new ConcurrentHashMap<>();

    private VotingSessionManager() {}

    public static void addVotingSession(VotingSession session) {
        if (session == null || session.getRoomCode() == null) {
            throw new IllegalArgumentException("VotingSession or roomCode must not be null");
        }
        sessions.put(session.getRoomCode(), session);
    }

    public static VotingSession getVotingSession(String roomCode) {
        VotingSession session = sessions.get(roomCode);
        if (session == null) {
            throw new IllegalStateException("No active voting session found for room: " + roomCode);
        }
        return session;
    }

    public static void removeVotingSession(String roomCode) {
        if (!sessions.containsKey(roomCode)) {
            throw new IllegalStateException("Cannot remove: no voting session found for room: " + roomCode);
        }
        sessions.remove(roomCode);
    }

    public static boolean isActive(String roomCode) {
        return sessions.containsKey(roomCode);
    }
}
