package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.entity.Game;
import ch.uzh.ifi.hase.soprafs25.service.image.ImageService;
import ch.uzh.ifi.hase.soprafs25.session.GameSessionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class ImageController {

    private final ImageService imageService;

    // Change mock to google to use real API
    public ImageController(@Qualifier("mockImageService") ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping(value = "/image/{roomCode}/{index}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public byte[] getGameImage(@PathVariable String roomCode, @PathVariable int index) {
        Game game = GameSessionManager.getGameSession(roomCode);
        return game.getImages().get(index);
    }
}

