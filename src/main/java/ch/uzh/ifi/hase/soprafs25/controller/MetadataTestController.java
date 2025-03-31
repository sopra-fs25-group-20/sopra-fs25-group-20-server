package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.service.image.StreetViewMetadataService;
import org.springframework.web.bind.annotation.*;

// To test real coordinates which give status:OK
// GET http://localhost:8080/test-metadata?lat=48.8584&lng=2.2945

// To test real coordinates which give status:ZERO_RESULTS
// GET http://localhost:8080/test-metadata?lat=0.0&lng=-160.0

@RestController
@RequestMapping("/test-metadata")
public class MetadataTestController {

    private final StreetViewMetadataService metadataService;

    public MetadataTestController(StreetViewMetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @GetMapping
    public String testMetadata(@RequestParam double lat, @RequestParam double lng) {
        return metadataService.getStatus(lat, lng);
    }
}
