package ch.uzh.ifi.hase.soprafs25.service.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Service("googleImageService")
public class GoogleImageService implements ImageService {

    private static final String IMAGE_API_URL = "https://maps.googleapis.com/maps/api/streetview";
    private static final int MAX_ATTEMPTS = 38;

    private final StreetViewMetadataService metadataService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${google.maps.api.key}")
    private String apiKey;

    public GoogleImageService(StreetViewMetadataService metadataService) {
        this.metadataService = metadataService;
    }

  ImageLoadingException(new Throwable("No StreetView image available after multiple attempts."));
    }

    private byte[] fetchStreetViewImage(double lat, double lng) {
        String url = UriComponentsBuilder.fromHttpUrl(IMAGE_API_URL)
                .queryParam("size", "400x400")
                .queryParam("location", lat + "," + lng)
                .queryParam("key", apiKey)
                .toUriString();

        return restTemplate.getForObject(url, byte[].class);
    }
}
