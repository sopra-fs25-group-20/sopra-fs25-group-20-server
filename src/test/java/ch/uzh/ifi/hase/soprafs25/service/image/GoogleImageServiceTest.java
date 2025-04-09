package ch.uzh.ifi.hase.soprafs25.service.image;

import ch.uzh.ifi.hase.soprafs25.exceptions.ImageLoadingException;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class GoogleImageServiceTest {

    @Mock
    private StreetViewMetadataService metadataService;

    @Mock
    private RestTemplate restTemplate;

    private GoogleImageService googleImageService;
    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
        googleImageService = new GoogleImageService(metadataService, restTemplate);
        ReflectionTestUtils.setField(googleImageService, "apiKey", "dummy-api-key");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testFetchImageByLocationWithValidLocation() {
        String testLocation = "asia";
        when(metadataService.getStatus(anyDouble(), anyDouble())).thenReturn("OK");
        byte[] expectedImage = new byte[]{7, 8, 9};
        when(restTemplate.getForObject(any(URI.class), eq(byte[].class))).thenReturn(expectedImage);

        byte[] result = googleImageService.fetchImageByLocation(testLocation);

        assertArrayEquals(expectedImage, result);
        verify(metadataService, atLeastOnce()).getStatus(anyDouble(), anyDouble());
        verify(restTemplate).getForObject(any(URI.class), eq(byte[].class));
    }

    @Test
    void testFetchImageByLocationNoSuccessfulAttempt() {
        String testLocation = "asia";
        when(metadataService.getStatus(anyDouble(), anyDouble())).thenReturn("ZERO_RESULTS");

        ImageLoadingException exception = assertThrows(ImageLoadingException.class, () ->
                googleImageService.fetchImageByLocation(testLocation)
        );

        assertTrue(exception.getErrorMessage().contains("Failed to load image"));
    }
}
