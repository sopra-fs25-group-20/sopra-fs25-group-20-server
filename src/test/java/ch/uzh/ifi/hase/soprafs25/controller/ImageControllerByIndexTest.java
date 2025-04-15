package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.entity.Game;
import ch.uzh.ifi.hase.soprafs25.session.GameSessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ImageControllerByIndexTest {

    private ImageController imageController;

    @BeforeEach
    void setUp() {
        imageController = new ImageController(null);

        Game game = new Game("ROOM1");
        game.setImages(List.of(
                new byte[]{10, 20},
                new byte[]{30, 40},
                new byte[]{50, 60}
        ));
        GameSessionManager.addGameSession(game);
    }

    @Test
    void testGetGameImage_validIndex_returnsImage() {
        byte[] result = imageController.getGameImage("ROOM1", 1);
        assertNotNull(result);
        assertArrayEquals(new byte[]{30, 40}, result);
    }

    @Test
    void testGetGameImage_invalidIndex_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            imageController.getGameImage("ROOM1", 99);
        });
    }

    @Test
    void testGetGameImage_invalidRoom_throwsException() {
        assertThrows(IllegalStateException.class, () -> {
            imageController.getGameImage("NON_EXISTENT", 0);
        });
    }
}
