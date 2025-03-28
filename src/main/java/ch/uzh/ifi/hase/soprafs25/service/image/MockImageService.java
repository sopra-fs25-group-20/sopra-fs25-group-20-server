package ch.uzh.ifi.hase.soprafs25.service.image;

import ch.uzh.ifi.hase.soprafs25.exceptions.ImageLoadingException;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.springframework.core.io.ClassPathResource;

@Service
public class    MockImageService implements ImageService {
    private static final Logger log = LoggerFactory.getLogger(MockImageService.class);

    @Override
    public byte[] fetchImage(double lat, double lng) {
        try {
            // load static image from class path
            ClassPathResource resource = new ClassPathResource("static/mock-image.jpg");
            InputStream inputStream = resource.getInputStream();
            return inputStream.readAllBytes();
        } catch (IOException e) {
            log.error("Error while loading mock image from path: static/mock-image.jpg", e);
            throw new ImageLoadingException(e);
        }
    }
}
