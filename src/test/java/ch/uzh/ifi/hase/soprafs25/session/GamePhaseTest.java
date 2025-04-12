package ch.uzh.ifi.hase.soprafs25.session;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GamePhaseTest {
    
    @Test
    public void testEnumValues() {
        GamePhase[] values = GamePhase.values();
        assertEquals(4, values.length);
        assertTrue(contains(values, GamePhase.WAITING));
        assertTrue(contains(values, GamePhase.ROUND));        
        assertTrue(contains(values, GamePhase.VOTING));
        assertTrue(contains(values, GamePhase.RESULT));
    }

    private boolean contains(GamePhase[] values, GamePhase target) {
        for (GamePhase value : values) {
            if (value == target) return true;
        }
        return false; 
    }

    @Test
    public void testEnumNameConsistency() {
        assertEquals("WAITING", GamePhase.WAITING.name());
        assertEquals("ROUND", GamePhase.ROUND.name());
        assertEquals("VOTING", GamePhase.VOTING.name());
        assertEquals("RESULT", GamePhase.RESULT.name());        
    }
}
