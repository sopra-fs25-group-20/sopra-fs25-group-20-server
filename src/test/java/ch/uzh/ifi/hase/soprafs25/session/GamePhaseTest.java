package ch.uzh.ifi.hase.soprafs25.session;

import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GamePhaseTest {
    
    @Test
    public void testEnumValues() {
        GamePhase[] values = GamePhase.values();
        assertEquals(4, values.length);
        assertTrue(contains(values, GamePhase.LOBBY));
        assertTrue(contains(values, GamePhase.GAME));
        assertTrue(contains(values, GamePhase.VOTE));
        assertTrue(contains(values, GamePhase.SUMMARY));
    }

    private boolean contains(GamePhase[] values, GamePhase target) {
        for (GamePhase value : values) {
            if (value == target) return true;
        }
        return false; 
    }

    @Test
    public void testEnumNameConsistency() {
        assertEquals("WAITING", GamePhase.LOBBY.name());
        assertEquals("ROUND", GamePhase.GAME.name());
        assertEquals("VOTING", GamePhase.VOTE.name());
        assertEquals("RESULT", GamePhase.SUMMARY.name());
    }
}
