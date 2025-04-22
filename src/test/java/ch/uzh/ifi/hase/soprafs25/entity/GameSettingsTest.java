package ch.uzh.ifi.hase.soprafs25.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameSettingsTest {

    @Test
    void testConstructorAndGetters() {
        GameSettings settings = new GameSettings(2, 30, 60, 6, "europe");
        assertEquals(2, settings.getSpyCount());
        assertEquals(30, settings.getVotingTimer());
        assertEquals(60, settings.getGameTimer());
        assertEquals(6, settings.getImageCount());
        assertEquals("europe", settings.getImageRegion());
    }

    @Test
    void testSetters() {
        GameSettings settings = new GameSettings(2, 30, 60, 6, "europe");
        settings.setVotingTimer(45);
        settings.setGameTimer(90);
        settings.setImageCount(9);
        settings.setImageRegion("asia");

        assertEquals(45, settings.getVotingTimer());
        assertEquals(90, settings.getGameTimer());
        assertEquals(9, settings.getImageCount());
        assertEquals("asia", settings.getImageRegion());
    }
}
