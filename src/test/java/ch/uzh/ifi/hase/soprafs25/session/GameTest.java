package ch.uzh.ifi.hase.soprafs25.session;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

public class GameTest {
    
    @Test
    public void testGameInitialization() {
        Game game = new Game("ROOM123");

        assertEquals("ROOM123", game.getRoomCode());
        assertEquals(GamePhase.WAITING, game.getPhase());
        assertEquals(0, game.getHighlightedImage());
        assertTrue(game.getRoles().isEmpty());
    }

    @Test
    public void testSetPhase() {
        Game game = new Game("ROOM456");
        game.setPhase(GamePhase.VOTING);
        assertEquals(GamePhase.VOTING, game.getPhase());
    }

    @Test
    public void testHighlightedImageSetterGetter() {
        Game game = new Game("ROOM789");
        game.setHighlightedImage(5);
        assertEquals(5, game.getHighlightedImage());
    }

    @Test
    public void testAssignAndGetRoles() {
        Game game = new Game("ROOM012");
        game.assignRole("testUser", "SPY");
        game.assignRole("testUser2", "INNOCENT");

        Map<String, String> roles = game.getRoles();

        assertEquals(2, roles.size());
        assertEquals("SPY", game.getRole("testUser"));
        assertEquals("INNOCENT", game.getRole("testUser2"));
    }
}
