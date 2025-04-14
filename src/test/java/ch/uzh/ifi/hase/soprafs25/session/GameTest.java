package ch.uzh.ifi.hase.soprafs25.session;

import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;
import ch.uzh.ifi.hase.soprafs25.entity.Game;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import java.util.List;

class GameTest {

    @Test
    void testGameInitialization() {
        Game game = new Game("ROOM123");

        assertEquals("ROOM123", game.getRoomCode());
        assertEquals(GamePhase.LOBBY, game.getPhase());
        assertEquals(0, game.getHighlightedImage());
        assertTrue(game.getRoles().isEmpty());
    }

    @Test
    void testSetPhase() {
        Game game = new Game("ROOM456");
        game.setPhase(GamePhase.VOTE);
        assertEquals(GamePhase.VOTE, game.getPhase());
    }

    @Test
    void testHighlightedImageSetterGetter() {
        Game game = new Game("ROOM789");
        game.setHighlightedImage(5);
        assertEquals(5, game.getHighlightedImage());
    }

    @Test
    void testAssignAndGetRoles() {
        Game game = new Game("ROOM012");
        List<String> nicknames = List.of("A", "B", "C");
        game.assignRoles(nicknames);

        Map<String, PlayerRole> roles = game.getRoles();

        assertEquals(3, roles.size());

        long spyCount = roles.values().stream().filter(role -> role == PlayerRole.SPY).count();
        long innocentCount = roles.values().stream().filter(role -> role == PlayerRole.INNOCENT).count();

        assertEquals(1, spyCount);
        assertEquals(2, innocentCount);
        assertTrue(roles.containsKey("A"));
        assertTrue(roles.containsKey("B"));
        assertTrue(roles.containsKey("C"));
    }

    @Test
    void testGetRoleByNickname() {
        Game game = new Game("ROOM012");
        game.assignRoles(List.of("X", "Y", "Z"));
        PlayerRole role = game.getRole("Y");

        assertNotNull(role);
        assertTrue(role == PlayerRole.INNOCENT || role == PlayerRole.SPY);
    }
}
