package ch.uzh.ifi.hase.soprafs25.service.image;

import ch.uzh.ifi.hase.soprafs25.exceptions.ImageLoadingException;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.io.ClassPathResource;

@Service
public class    MockImageService implements ImageService {

    @Override
    public byte[] fetchImage() {
        try {
            // load static image from class path
            ClassPathResource resource = new ClassPathResource("static/mock-image.jpg");
            InputStream inputStream = resource.getInputStream();
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new ImageLoadingException(e);
        }
    }
}
