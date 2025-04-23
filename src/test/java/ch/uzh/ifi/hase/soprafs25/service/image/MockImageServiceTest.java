package ch.uzh.ifi.hase.soprafs25.service.image;

import ch.uzh.ifi.hase.soprafs25.exceptions.ImageLoadingException;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.*;

class MockImageServiceTest {

    private final MockImageService imageService = new MockImageService();

    @Test
    void fetchImage_returnsNonEmptyByteArray() {
        byte[] image = imageService.fetchImage();
        assertThat(image).isNotNull().isNotEmpty();
    }

    @Test
    void fetchImageByLocation_delegatesToFetchImage() {
        byte[] fromLocation = imageService.fetchImageByLocation("europe");
        byte[] fromDefault = imageService.fetchImage();

        assertThat(fromLocation).isEqualTo(fromDefault);
    }

    @Test
    void fetchImageByLocationAsync_returnsCompletableFuture() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<byte[]> future = imageService.fetchImageByLocationAsync("europe");
        byte[] result = future.get(1, TimeUnit.SECONDS);

        assertThat(result).isNotNull().isNotEmpty();
        assertThat(result).isEqualTo(imageService.fetchImage());
    }

    @Test
    void fetchImage_throwsExceptionIfFileNotFound() {
        // simulate failure by using a subclass with wrong path
        MockImageService brokenService = new MockImageService() {
            @Override
            public byte[] fetchImage() {
                try {
                    return new ClassPathResource("nonexistent.jpg").getInputStream().readAllBytes();
                } catch (IOException e) {
                    throw new ImageLoadingException(e);
                }
            }
        };

        assertThatThrownBy(brokenService::fetchImage)
                .isInstanceOf(ImageLoadingException.class);
    }
}
