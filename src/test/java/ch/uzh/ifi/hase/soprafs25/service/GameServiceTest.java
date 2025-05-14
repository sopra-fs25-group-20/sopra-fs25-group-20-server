package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;
import ch.uzh.ifi.hase.soprafs25.entity.Game;
import ch.uzh.ifi.hase.soprafs25.model.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs25.service.image.ImageService;
import ch.uzh.ifi.hase.soprafs25.session.GameSessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

class GameServiceTest {

    @Mock private AuthorizationService authorizationService;
    @Mock private GameReadService gameReadService;
    @Mock private GameBroadcastService gameBroadcastService;
    @Mock private ImageService imageService;
    @SuppressWarnings("unused")
    @Mock private GameTimerService gameTimerService;

    @InjectMocks private GameService gameService;
    private AutoCloseable mocks;

    @BeforeEach
    void setUp() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);
        clearSessions();
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
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
        game.setGameSettings(1, 30, 3, "europe");

        byte[] mockImage = new byte[]{1,2,3};
        List<CompletableFuture<byte[]>> futures = IntStream.range(0,3)
                .mapToObj(i -> CompletableFuture.completedFuture(mockImage))
                .toList();
        when(imageService.fetchImagesByLocationAsync("europe", 3)).thenReturn(futures);

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
    void createGame_successfullyAddsSession() {
        String code = "R1";
        gameService.createGame(code);
        Game session = GameSessionManager.getGameSession(code);
        assertNotNull(session, "Game session should be created");
        assertEquals(code, session.getRoomCode());
    }

    @Test
    void createGame_whenAlreadyActive_throwsException() {
        String code = "R2";
        GameSessionManager.addGameSession(new Game(code));
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> gameService.createGame(code));
        assertTrue(ex.getMessage().contains("Game already active"));
    }

    @Test
    void advancePhase_setsPhaseAndBroadcasts() {
        String code = "R3";
        Game game = new Game(code);
        GameSessionManager.addGameSession(game);

        gameService.advancePhase(code, GamePhase.SUMMARY);

        Game updated = GameSessionManager.getGameSession(code);
        assertEquals(GamePhase.SUMMARY, updated.getPhase());
        then(gameBroadcastService).should().broadcastGamePhase(code);
    }

    @Test
    void startRound_asAdmin_preparesGameAndBroadcastsPhase() {
        // given
        String code  = "C1";
        String admin = "alice";

        Game game = new Game(code);
        game.setGameSettings(1, 30, 2, "loc");
        GameSessionManager.addGameSession(game);
        when(authorizationService.isAdmin(code, admin)).thenReturn(true);
        when(gameReadService.getNicknamesInRoom(code)).thenReturn(List.of(admin));
        byte[] img = new byte[]{9};
        List<CompletableFuture<byte[]>> futures = List.of(
                CompletableFuture.completedFuture(img),
                CompletableFuture.completedFuture(img)
        );
        when(imageService.fetchImagesByLocationAsync("loc", 2)).thenReturn(futures);

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
        String code = "C2";
        String user = "bob";
        GameSessionManager.addGameSession(new Game(code));
        when(authorizationService.isAdmin(code, user)).thenReturn(false);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> gameService.startRound(code, user)
        );
        assertTrue(ex.getMessage().contains("Only admin"));
    }

    @Test
    void handleSpyGuess_correctGuess_setsSpyResult() {
        String code = "R4";
        String spy = "spyUser";
        Game game = new Game(code);
        game.setHighlightedImageIndex(1);
        game.assignRoles(List.of(spy));
        GameSessionManager.addGameSession(game);

        gameService.handleSpyGuess(code, spy, 1);

        Game resultGame = GameSessionManager.getGameSession(code);
        assertEquals(PlayerRole.SPY, resultGame.getGameResult().getWinnerRole());
        assertEquals(1, resultGame.getGameResult().getSpyGuessIndex());
        then(gameBroadcastService).should().broadcastGamePhase(code);
    }

    @Test
    void handleSpyGuess_wrongGuess_setsInnocentResult() {
        String code = "R5";
        String spy = "spyUser";
        Game game = new Game(code);
        game.setHighlightedImageIndex(2);
        game.assignRoles(List.of(spy));
        GameSessionManager.addGameSession(game);

        gameService.handleSpyGuess(code, spy, 0);

        Game resultGame = GameSessionManager.getGameSession(code);
        assertEquals(PlayerRole.INNOCENT, resultGame.getGameResult().getWinnerRole());
        then(gameBroadcastService).should().broadcastGamePhase(code);
    }

    @Test
    void changeGameSettings_asAdmin_broadcastsSettings() {
        String code = "R6";
        String admin = "admin";
        Game game = new Game(code);
        GameSessionManager.addGameSession(game);
        when(authorizationService.isAdmin(code, admin)).thenReturn(true);
        GameSettingsDTO dto = new GameSettingsDTO(5, 10, 4, "reg");

        gameService.changeGameSettings(code, admin, dto);

        Game updated = GameSessionManager.getGameSession(code);
        assertEquals(5, updated.getGameSettings().getVotingTimer());
        then(gameBroadcastService).should().broadcastGameSettings(code);
    }

    @Test
    void changeGameSettings_nonAdmin_throwsException() {
        String code = "R7";
        String user = "user";
        GameSessionManager.addGameSession(new Game(code));
        when(authorizationService.isAdmin(code, user)).thenReturn(false);

        GameSettingsDTO settings = new GameSettingsDTO(1, 1, 1, "x");

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                gameService.changeGameSettings(code, user, settings)
        );

        assertTrue(ex.getMessage().contains("Only admin"));
    }

    @Test
    void broadcastPersonalizedRole_delegatesToService() {
        gameService.broadcastPersonalizedRole("R8", "U");
        then(gameBroadcastService).should().broadcastPersonalizedRole("R8", "U");
    }

    @Test
    void broadcastPersonalizedImageIndex_delegatesToService() {
        gameService.broadcastPersonalizedImageIndex("R9", "V");
        then(gameBroadcastService).should().broadcastPersonalizedImageIndex("R9", "V");
    }
}