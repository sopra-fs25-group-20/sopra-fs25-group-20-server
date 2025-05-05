package ch.uzh.ifi.hase.soprafs25.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserGetDTOTest {

    @Test
    void testConstructorAndGetters() {
        UserGetDTO dto = new UserGetDTO("player1", 12, 3, 15, 4, 6);

        assertEquals("player1", dto.getUsername());
        assertEquals(12, dto.getWins());
        assertEquals(3, dto.getDefeats());
        assertEquals(15, dto.getGames());
        assertEquals(4, dto.getCurrentStreak());
        assertEquals(6, dto.getHighestStreak());
    }

    @Test
    void testSetters() {
        UserGetDTO dto = new UserGetDTO("tmp", 0, 0, 0, 0, 0);

        dto.setUsername("testuser");
        dto.setWins(10);
        dto.setDefeats(2);
        dto.setGames(12);
        dto.setCurrentStreak(1);
        dto.setHighestStreak(5);

        assertEquals("testuser", dto.getUsername());
        assertEquals(10, dto.getWins());
        assertEquals(2, dto.getDefeats());
        assertEquals(12, dto.getGames());
        assertEquals(1, dto.getCurrentStreak());
        assertEquals(5, dto.getHighestStreak());
    }
}
