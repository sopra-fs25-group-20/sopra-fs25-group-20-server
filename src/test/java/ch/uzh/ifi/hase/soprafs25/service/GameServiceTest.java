package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs25.entity.Game;
import ch.uzh.ifi.hase.soprafs25.service.image.ImageService;
import ch.uzh.ifi.hase.soprafs25.session.GameSessionManager;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class GameServiceTest {

    @Mock AuthorizationService authorizationService;
    @Mock GameReadService  gameReadService;
    @Mock GameBroadcastService gameBroadcastService;
    @Mock ImageService     imageService;

    @InjectMocks
    private GameService gameService;

    @SuppressWarnings("resource")
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        clearSessions();
    }

    @AfterEach
    void tearDown() throws Exception {
        clearSessions();
    }

    private void clearSessions() throws Exception {
        Field f = GameSessionManager.class.getDeclaredField("gameSessions");
        f.setAccessible(true);
        ((Map<?,?>)f.get(null)).clear();
    }

    @Test
    void prepareImagesForRound_addsImagesCorrectly() throws Exception {
        // given
        Game game = new Game("ROOM123");
        // use ints, not longs
        game.setGameSettings(1, 30, 3, "europe");

        byte[] mockImage = new byte[]{1,2,3};
        List<CompletableFuture<byte[]>> futures = IntStream.range(0,3)
                .mapToObj(i -> CompletableFuture.completedFuture(mockImage))
                .toList();
        given(imageService.fetchImagesByLocationAsync("europe", 3))
                .willReturn(futures);

        // when: invoke private prepareImagesForRound(Game)
        Method m = GameService.class.getDeclaredMethod("prepareImagesForRound", Game.class);
        m.setAccessible(true);
        m.invoke(gameService, game);

        // then
        List<byte[]> images = game.getImages();
        assertEquals(3, images.size(), "Should load 3 images");
        for (byte[] img : images) {
            assertArrayEquals(mockImage, img);
        }
        then(imageService).should().fetchImagesByLocationAsync("europe", 3);
    }

    @Test
    void startRound_asAdmin_preparesGameAndBroadcastsPhase() {
        // given
        String code  = "C1";
        String admin = "alice";

        Game game = new Game(code);
        game.setGameSettings(1, 30, 2, "loc");
        GameSessionManager.addGameSession(game);

        given(authorizationService.isAdmin(code, admin)).willReturn(true);
        given(gameReadService.getNicknamesInRoom(code)).willReturn(List.of(admin));

        byte[] img = new byte[]{9};
        List<CompletableFuture<byte[]>> futures = List.of(
                CompletableFuture.completedFuture(img),
                CompletableFuture.completedFuture(img)
        );
        given(imageService.fetchImagesByLocationAsync("loc", 2)).willReturn(futures);

        // when
        gameService.startRound(code, admin);

        // then
        Game started = GameSessionManager.getGameSession(code);
        assertEquals(GamePhase.GAME, started.getPhase());
        assertEquals(2, started.getImages().size());
        then(gameBroadcastService).should().broadcastGamePhase(code);
    }

    @Test
    void startRound_nonAdmin_throws() {
        // given
        String code = "C2";
        String user = "bob";
        GameSessionManager.addGameSession(new Game(code));
        given(authorizationService.isAdmin(code, user)).willReturn(false);

        // when / then
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> gameService.startRound(code, user)
        );
        assertTrue(ex.getMessage().contains("Only admin"));
    }
}
