package ch.uzh.ifi.hase.soprafs25.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    @Test
    void testUserGettersAndSetters() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("user1");
        user.setPassword("pass123");
        user.setToken("tokenABC");
        user.setWins(10);
        user.setDefeats(5);
        user.setGames(15);
        user.setCurrentStreak(3);
        user.setHighestStreak(7);

        assertEquals(1L, user.getUserId());
        assertEquals("user1", user.getUsername());
        assertEquals("pass123", user.getPassword());
        assertEquals("tokenABC", user.getToken());
        assertEquals(10, user.getWins());
        assertEquals(5, user.getDefeats());
        assertEquals(15, user.getGames());
        assertEquals(3, user.getCurrentStreak());
        assertEquals(7, user.getHighestStreak());
    }

    @Test
    void testRecordWinAndStreaks() {
        User user = new User();
        user.setCurrentStreak(2);
        user.setHighestStreak(3);
        user.setWins(5);
        user.setGames(7);

        user.recordWin();
        assertEquals(6, user.getWins());
        assertEquals(8, user.getGames());
        assertEquals(3, user.getCurrentStreak());
        assertEquals(3, user.getHighestStreak());

        user.recordWin();
        assertEquals(4, user.getCurrentStreak());
        assertEquals(4, user.getHighestStreak());
    }

    @Test
    void testRecordDefeat() {
        User user = new User();
        user.setDefeats(2);
        user.setGames(5);
        user.setCurrentStreak(4);

        user.recordDefeat();
        assertEquals(3, user.getDefeats());
        assertEquals(6, user.getGames());
        assertEquals(0, user.getCurrentStreak());
    }
}
