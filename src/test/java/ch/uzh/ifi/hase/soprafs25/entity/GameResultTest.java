package ch.uzh.ifi.hase.soprafs25.entity;

import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameResultTest {

    @Test
    void testConstructorAndGetters() {
        GameResult result = new GameResult(1, "alice", PlayerRole.SPY);
        assertEquals(1, result.getSpyGuessIndex());
        assertEquals("alice", result.getVotedNickname());
        assertEquals(PlayerRole.SPY, result.getWinnerRole());
    }

    @Test
    void testSetters() {
        GameResult result = new GameResult(1, "alice", PlayerRole.SPY);
        result.setSpyGuessIndex(2);
        result.setVotedNickname("bob");
        result.setWinnerRole(PlayerRole.INNOCENT);

        assertEquals(2, result.getSpyGuessIndex());
        assertEquals("bob", result.getVotedNickname());
        assertEquals(PlayerRole.INNOCENT, result.getWinnerRole());
    }
}
