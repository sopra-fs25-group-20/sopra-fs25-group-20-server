package ch.uzh.ifi.hase.soprafs25.session;

import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs25.entity.Game;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import java.util.List;

public class GameTest {

    @Test
    public void testGameInitialization() {
        Game game = new Game("ROOM123");

        assertEquals("ROOM123", game.getRoomCode());
        assertEquals(GamePhase.LOBBY, game.getPhase());
        assertEquals(0, game.getHighlightedImage());
        assertTrue(game.getRoles().isEmpty());
    }

    @Test
    public void testSetPhase() {
        Game game = new Game("ROOM456");
        game.setPhase(GamePhase.VOTE);
        assertEquals(GamePhase.VOTE, game.getPhase());
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
        List<String> nicknames = List.of("A", "B", "C");
        game.assignRoles(nicknames);

        Map<String, String> roles = game.getRoles();

        assertEquals(3, roles.size());
        long spyCount = roles.values().stream().filter(role -> role.equals("spy")).count();
        long innocentCount = roles.values().stream().filter(role -> role.equals("innocent")).count();

        assertEquals(1, spyCount);
        assertEquals(2, innocentCount);
        assertTrue(roles.containsKey("A"));
        assertTrue(roles.containsKey("B"));
        assertTrue(roles.containsKey("C"));
    }

    @Test
    public void testGetRoleByNickname() {
        Game game = new Game("ROOM012");
        game.assignRoles(List.of("X", "Y", "Z"));
        String role = game.getRole("Y");
        assertNotNull(role);
        assertTrue(role.equals("spy") || role.equals("innocent"));
    }
}
