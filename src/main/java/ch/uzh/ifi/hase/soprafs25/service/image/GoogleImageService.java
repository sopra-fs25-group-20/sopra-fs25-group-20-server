package ch.uzh.ifi.hase.soprafs25.service.image;

import ch.uzh.ifi.hase.soprafs25.exceptions.ImageLoadingException;
import ch.uzh.ifi.hase.soprafs25.util.CoordinatesUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.SecureRandom;
import java.util.Map;

@Service("googleImageService")
public class GoogleImageService implements ImageService {

    private static final int MAX_ATTEMPTS = 100;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final StreetViewMetadataService metadataService;
    private final RestTemplate restTemplate;

    @Value("${google.maps.api.key}")
    private String apiKey;

    public GoogleImageService(StreetViewMetadataService metadataService, RestTemplate restTemplate) {
        this.metadataService = metadataService;
        this.restTemplate = restTemplate;
    }

    /**
     * Uses completely random coordinates worldwide.
     */
    @Override
    public byte[] fetchImage() {
        return fetchImageWithAttempts(null);
    }

    /**
     * Uses bounding box from coordinates.json if location is provided.
     */
    @Override
    public byte[] fetchImageByLocation(String location) {
        return fetchImageWithAttempts(location);
    }

    private byte[] fetchImageWithAttempts(String location) {
        int attempt = 0;

        while (attempt < MAX_ATTEMPTS) {
            double lat;
            double lng;

            if (location != null) {
                Map<String, Double> coords = CoordinatesUtil.getRandomCoordinate(location);
                if (coords.isEmpty()) {
                    throw new ImageLoadingException(new Throwable("Invalid location: " + location));
                }
                lat = coords.get("lat");
                lng = coords.get("lng");
            } else {
                lat = -90.0 + 180.0 * SECURE_RANDOM.nextDouble();
                lng = -180.0 + 360.0 * SECURE_RANDOM.nextDouble();
            }

            String status = metadataService.getStatus(lat, lng);
            if ("OK".equals(status)) {
                return fetchStreetViewImage(lat, lng);
            }
            attempt++;
        }

        throw new ImageLoadingException(new Throwable("No StreetView image available after " + MAX_ATTEMPTS + " attempts."));
    }

    private byte[] fetchStreetViewImage(double lat, double lng) {
        URI uri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("maps.googleapis.com")
                .path("/maps/api/streetview")
                .queryParam("size", "400x400")
                .queryParam("location", lat + "," + lng)
                .queryParam("radius", 5000)
                .queryParam("source", "outdoor")
                .queryParam("key", apiKey)
                .build()
                .encode()
                .toUri();

        try {
            return restTemplate.getForObject(uri, byte[].class);
        } catch (Exception e) {
            throw new ImageLoadingException(e);
        }
    }
}
