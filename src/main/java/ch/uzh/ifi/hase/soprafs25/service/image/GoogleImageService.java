package ch.uzh.ifi.hase.soprafs25.service.image;

import ch.uzh.ifi.hase.soprafs25.exceptions.ImageLoadingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.concurrent.ThreadLocalRandom;

@Service("googleImageService")
public class GoogleImageService implements ImageService {

    private static final int MAX_ATTEMPTS = 30;

    private final StreetViewMetadataService metadataService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${google.maps.api.key}")
    private String apiKey;

    public GoogleImageService(StreetViewMetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @Override
    public byte[] fetchImage() {
        int attempt = 0;

        while (attempt < MAX_ATTEMPTS) {
            double lat = ThreadLocalRandom.current().nextDouble(-90.0, 90.0); //NOSONAR
            double lng = ThreadLocalRandom.current().nextDouble(-180.0, 180.0); //NOSONAR

            String status = metadataService.getStatus(lat, lng);

            if ("OK".equals(status)) {
                return fetchStreetViewImage(lat, lng);
            }

            attempt++;
        }

        throw new ImageLoadingException(new Throwable("No StreetView image available after multiple attempts."));
    }

    private byte[] fetchStreetViewImage(double lat, double lng) {
        URI uri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("maps.googleapis.com")
                .path("/maps/api/streetview")
                .queryParam("size", "400x400")
                .queryParam("location", lat + "," + lng)
                .queryParam("radius", 50000)
                .queryParam("key", apiKey)
                .build()
                .encode()
                .toUri();

        return restTemplate.getForObject(uri, byte[].class);
    }

    @Override
    public byte[] fetchImage(String location) {
        return fetchImage();
    }
}

