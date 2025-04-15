package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.service.image.ImageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import ch.uzh.ifi.hase.soprafs25.entity.Game;
import ch.uzh.ifi.hase.soprafs25.session.GameSessionManager;

@RestController
public class ImageController {

    private final ImageService imageService;

    // Change mock to google to use real API
    public ImageController(@Qualifier("googleImageService") ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping(value = "/image", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> getImage(@RequestParam(required = false) String location) {
        byte[] image = (location == null)
                ? imageService.fetchImage()
                : imageService.fetchImageByLocation(location);
        return ResponseEntity.ok(image);
    }

    @GetMapping(value = "/image/{roomCode}/{index}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public byte[] getGameImage(@PathVariable String roomCode, @PathVariable int index) {
        Game game = GameSessionManager.getGameSession(roomCode);
        return game.getImages().get(index);
    }


}

