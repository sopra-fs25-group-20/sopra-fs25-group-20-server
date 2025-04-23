package ch.uzh.ifi.hase.soprafs25.service.image;

import ch.uzh.ifi.hase.soprafs25.exceptions.CoordinatesLoadingException;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({ "rawtypes", "unchecked" })
class GoogleImageServiceTest {

    @Mock private StreetViewMetadataService metadataService;
    @Mock private WebClient webClient;
    @Mock private WebClient.RequestHeadersUriSpec uriSpec;
    @Mock private WebClient.RequestHeadersSpec<?> headersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    private GoogleImageService googleImageService;
    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        googleImageService = new GoogleImageService(metadataService, webClient);
        ReflectionTestUtils.setField(googleImageService, "apiKey", "dummy-key");
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void fetchImage_whenStatusOk_returnsBytes() {
        when(metadataService.getStatus(anyDouble(), anyDouble())).thenReturn("OK");
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        byte[] expected = {1, 2, 3};
        when(responseSpec.bodyToMono(byte[].class)).thenReturn(Mono.just(expected));

        byte[] actual = googleImageService.fetchImage();

        assertArrayEquals(expected, actual);
        verify(metadataService, atLeastOnce()).getStatus(anyDouble(), anyDouble());
    }

    @Test
    void fetchImageByLocation_whenStatusOk_returnsBytes() {
        when(metadataService.getStatus(anyDouble(), anyDouble())).thenReturn("OK");
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        byte[] expected = {4, 5, 6};
        when(responseSpec.bodyToMono(byte[].class)).thenReturn(Mono.just(expected));

        byte[] actual = googleImageService.fetchImageByLocation("europe");

        assertArrayEquals(expected, actual);
        verify(metadataService).getStatus(anyDouble(), anyDouble());
    }

    @Test
    void fetchImageByLocationAsync_whenStatusOk_returnsBytes() throws Exception {
        when(metadataService.getStatus(anyDouble(), anyDouble())).thenReturn("OK");
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        byte[] expected = {7, 8, 9};
        when(responseSpec.bodyToMono(byte[].class)).thenReturn(Mono.just(expected));

        CompletableFuture<byte[]> future = googleImageService.fetchImageByLocationAsync("asia");
        byte[] actual = future.get(5, TimeUnit.SECONDS);

        assertArrayEquals(expected, actual);
        verify(metadataService).getStatus(anyDouble(), anyDouble());
    }

    @Test
    void fetchImageByLocationAsync_whenInvalidLocation_completesExceptionallyWithCoordinatesLoadingException() {
        CompletableFuture<byte[]> future = googleImageService.fetchImageByLocationAsync("nowhere");

        CompletionException thrown = assertThrows(
                CompletionException.class,
                future::join
        );

        Throwable cause = thrown.getCause();
        assertTrue(cause instanceof CoordinatesLoadingException);
        assertTrue(cause.getCause() instanceof IllegalArgumentException);
        assertTrue(cause.getCause().getMessage().contains("Invalid region"));
    }
}
