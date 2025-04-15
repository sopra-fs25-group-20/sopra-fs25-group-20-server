package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.Game;
import ch.uzh.ifi.hase.soprafs25.service.image.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameServiceTest {

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private GameReadService gameReadService;

    @Mock
    private GameBroadcastService gameBroadcastService;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private GameService gameService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameService = new GameService(authorizationService, gameReadService, gameBroadcastService, imageService);
    }

    @Test
    void testPrepareImagesForRound_addsImagesCorrectly() {
        // given
        Game game = new Game("ROOM123");
        game.setGameSettings(1, 30, 3, "europe");

        byte[] mockImage = new byte[]{1, 2, 3};
        when(imageService.fetchImageByLocation("europe")).thenReturn(mockImage);

        // when
        Method method = ReflectionUtils.findMethod(GameService.class, "prepareImagesForRound", Game.class);
        assertNotNull(method, "Method 'prepareImagesForRound' should exist");
        method.setAccessible(true);
        ReflectionUtils.invokeMethod(method, gameService, game);

        // then
        List<byte[]> images = game.getImages();
        assertEquals(3, images.size(), "There should be 3 images loaded");
        for (byte[] image : images) {
            assertArrayEquals(mockImage, image, "Image bytes should match mock result");
        }

        verify(imageService, times(3)).fetchImageByLocation("europe");
    }
}