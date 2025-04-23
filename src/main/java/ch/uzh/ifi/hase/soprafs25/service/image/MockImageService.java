package ch.uzh.ifi.hase.soprafs25.service.image;

import ch.uzh.ifi.hase.soprafs25.exceptions.ImageLoadingException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

@Service("mockImageService")
public class MockImageService implements ImageService {

    @Override
    public byte[] fetchImage() {
        try {
            ClassPathResource resource = new ClassPathResource("static/mock-image.jpg");
            try (InputStream inputStream = resource.getInputStream()) {
                return inputStream.readAllBytes();
            }
        } catch (IOException e) {
            throw new ImageLoadingException(e);
        }
    }

    @Override
    public byte[] fetchImageByLocation(String location) {
        return fetchImage();
    }

    @Override
    public CompletableFuture<byte[]> fetchImageByLocationAsync(String location) {
        return CompletableFuture.completedFuture(fetchImageByLocation(location));
    }
}
