package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.service.image.ImageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class ImageController {

    private final ImageService imageService;

    // Change mock to google to use real API
    public ImageController(@Qualifier("mockImageService") ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping(value = "/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage() {

        double lat = ThreadLocalRandom.current().nextDouble(-90.0, 90.0);
        double lng = ThreadLocalRandom.current().nextDouble(-180.0, 180.0);

        byte[] image = imageService.fetchImage(lat, lng);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }
}
