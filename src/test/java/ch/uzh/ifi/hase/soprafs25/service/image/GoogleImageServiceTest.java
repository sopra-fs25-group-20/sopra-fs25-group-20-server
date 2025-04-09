package ch.uzh.ifi.hase.soprafs25.service.image;

import ch.uzh.ifi.hase.soprafs25.exceptions.ImageLoadingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class GoogleImageServiceTest {

    @Mock
    private StreetViewMetadataService metadataService;

    @Mock
    private RestTemplate restTemplate;

    private GoogleImageService googleImageService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        googleImageService = new GoogleImageService(metadataService, restTemplate);
        // Test için dummy API key ayarla.
        ReflectionTestUtils.setField(googleImageService, "apiKey", "dummy-api-key");
    }

    @Test
    public void testFetchImageByLocationWithValidLocation() {
        String testLocation = "istanbul";
        // Geçerli durum: metadata "OK" dönsün
        when(metadataService.getStatus(anyDouble(), anyDouble())).thenReturn("OK");
        byte[] expectedImage = new byte[]{7, 8, 9};
        when(restTemplate.getForObject(any(URI.class), eq(byte[].class))).thenReturn(expectedImage);

        byte[] result = googleImageService.fetchImageByLocation(testLocation);

        assertArrayEquals(expectedImage, result);
        verify(metadataService, atLeastOnce()).getStatus(anyDouble(), anyDouble());
        verify(restTemplate).getForObject(any(URI.class), eq(byte[].class));
    }

    @Test
    public void testFetchImageByLocationNoSuccessfulAttempt() {
        String testLocation = "tokyo";
        when(metadataService.getStatus(anyDouble(), anyDouble())).thenReturn("ZERO_RESULTS");

        ImageLoadingException exception = assertThrows(ImageLoadingException.class, () -> {
            googleImageService.fetchImageByLocation(testLocation);
        });
        assertTrue(exception.getErrorMessage().contains("Failed to load image"));
    }
}
