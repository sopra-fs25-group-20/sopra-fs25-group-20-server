package ch.uzh.ifi.hase.soprafs25.session;

import static org.junit.jupiter.api.Assertions.*;

import ch.uzh.ifi.hase.soprafs25.entity.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameSessionManagerTest {

    private Game testGame;

    @BeforeEach
    void setup() {
        if (GameSessionManager.isActive("ROOM123")) {
            GameSessionManager.removeGameSession("ROOM123");
        }
        testGame = new Game("ROOM123");
    }

    @Test
    void addAndGetGameSessionSuccessfully() {
        GameSessionManager.addGameSession(testGame);

        Game retrieved = GameSessionManager.getGameSession("ROOM123");
        assertNotNull(retrieved);
        assertEquals("ROOM123", retrieved.getRoomCode());
    }

    @Test
    void getGameSessionThrowsIfNotFound() {
        assertThrows(IllegalStateException.class, () -> GameSessionManager.getGameSession("NON_EXISTENT"));
    }

    @Test
    void removeGameSessionSuccessfully() {
        GameSessionManager.addGameSession(testGame);
        assertTrue(GameSessionManager.isActive("ROOM123"));

        GameSessionManager.removeGameSession("ROOM123");

        assertFalse(GameSessionManager.isActive("ROOM123"));
    }

    @Test
    void removeGameSessionThrowsIfNotFound() {
        assertThrows(IllegalStateException.class, () -> GameSessionManager.removeGameSession("UNKNOWN"));
    }

    @Test
    void addGameSessionThrowsIfGameIsNull() {
        assertThrows(IllegalStateException.class, () -> GameSessionManager.addGameSession(null));
    }

    @Test
    void addGameSessionThrowsIfRoomCodeIsNull() {
        Game badGame = new Game(null);
        assertThrows(IllegalStateException.class, () -> GameSessionManager.addGameSession(badGame));
    }

    @Test
    void isActiveReturnsTrueOnlyWhenAdded() {
        assertFalse(GameSessionManager.isActive("ROOM123"));
        GameSessionManager.addGameSession(testGame);
        assertTrue(GameSessionManager.isActive("ROOM123"));
    }
}
