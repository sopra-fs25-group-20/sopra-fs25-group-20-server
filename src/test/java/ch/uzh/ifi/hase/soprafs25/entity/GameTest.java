package ch.uzh.ifi.hase.soprafs25.entity;

import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void testDefaultValues() {
        Game game = new Game("ROOM123");
        assertEquals("ROOM123", game.getRoomCode());
        assertEquals(GamePhase.LOBBY, game.getPhase());
        assertNotNull(game.getGameSettings());
        assertTrue(game.getImages().isEmpty());
    }

    @Test
    void testRoleAssignmentAndRetrieval() {
        Game game = new Game("ROOM123");
        List<String> players = List.of("alice", "bob", "charlie");
        game.getGameSettings().setVotingTimer(20);
        game.assignRoles(players);

        Map<String, PlayerRole> roles = game.getRoles();
        assertEquals(3, roles.size());
        assertTrue(roles.containsKey("alice"));
        assertNotNull(game.getRole("alice"));
    }

    @Test
    void testGameResultSetting() {
        Game game = new Game("ROOM123");
        game.setGameResult(2, "bob", PlayerRole.SPY);
        GameResult result = game.getGameResult();
        assertEquals(2, result.getSpyGuessIndex());
        assertEquals("bob", result.getVotedNickname());
        assertEquals(PlayerRole.SPY, result.getWinnerRole());
    }

    @Test
    void testSetImagesAndClearImages() {
        Game game = new Game("ROOM123");
        byte[] img = new byte[]{1, 2, 3};
        game.setImages(List.of(img));
        assertEquals(1, game.getImages().size());

        game.clearImages();
        assertTrue(game.getImages().isEmpty());
    }
}
