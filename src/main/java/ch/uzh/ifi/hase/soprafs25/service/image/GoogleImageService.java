package ch.uzh.ifi.hase.soprafs25.service.image;

import ch.uzh.ifi.hase.soprafs25.exceptions.ImageLoadingException;
import ch.uzh.ifi.hase.soprafs25.util.CoordinatesUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Service("googleImageService")
public class GoogleImageService implements ImageService {

    private static final int MAX_ATTEMPTS = 100;
    private static final Random RAND = new Random();    // NOSONAR

    private final Executor executor = Executors.newFixedThreadPool(10);

    private final StreetViewMetadataService metadataService;
    private final RestTemplate restTemplate;

    @Value("${google.maps.api.key}")
    private String apiKey;

    public GoogleImageService(StreetViewMetadataService metadataService, RestTemplate restTemplate) {
        this.metadataService  = metadataService;
        this.restTemplate     = restTemplate;
    }

    @Override
    public byte[] fetchImage() {
        return fetchImageWithAttempts(null);
    }

    @Override
    public byte[] fetchImageByLocation(String location) {
        return fetchImageWithAttempts(location);
    }

    @Override
    public CompletableFuture<byte[]> fetchImageByLocationAsync(String location) {
        return CompletableFuture.supplyAsync(() -> fetchImageWithAttempts(location), executor);
    }


    @Override
    public List<CompletableFuture<byte[]>> fetchImagesByLocationAsync(String location, int count) {
        List<Coordinate> oks = new ArrayList<>();
        while (oks.size() < count) {
            // sample more coords to fill the remainder
            int needed = count - oks.size();
            List<Coordinate> batch = CoordinatesUtil.getRandomCoordinates(location, needed * 3);
            List<String> statuses = metadataService.getStatuses(batch);
            for (int i = 0; i < statuses.size() && oks.size() < count; i++) {
                if ("OK".equals(statuses.get(i))) {
                    oks.add(batch.get(i));
                }
            }
            if (batch.isEmpty()) break;  // give up to avoid infinite loop
        }
        if (oks.size() < count) {
            throw new ImageLoadingException(
                    new Throwable("Couldn’t find enough StreetView points (“OK”) for region=" + location));
        }

        return IntStream.range(0, count)
                .mapToObj(i ->
                        CompletableFuture.supplyAsync(
                                () -> fetchImageWithAttempts(location),
                                executor
                        )
                )
                .toList();
    }

    private byte[] fetchImageWithAttempts(String location) {
        int attempt = 0;
        while (attempt < MAX_ATTEMPTS) {
            double lat, lng;
            if (location != null) {
                Map<String, Double> m = CoordinatesUtil.getRandomCoordinate(location);
                if (m.isEmpty()) {
                    throw new ImageLoadingException(new Throwable("Invalid location: " + location));
                }
                lat = m.get("lat");
                lng = m.get("lng");
            } else {
                lat = -90.0 + 180.0 * RAND.nextDouble();
                lng = -180.0 + 360.0 * RAND.nextDouble();
            }
            if ("OK".equals(metadataService.getStatus(lat, lng))) {
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
