package ch.uzh.ifi.hase.soprafs25.service.image;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;


@Service
public class StreetViewMetadataService {

    private static final Logger log = LoggerFactory.getLogger(StreetViewMetadataService.class);
    private final WebClient googleMapsClient;

    @Value("${google.maps.api.key}")
    private String apiKey;


    public StreetViewMetadataService(WebClient googleMapsClient) {
        this.googleMapsClient = googleMapsClient;
    }

    public String getStatus(double lat, double lng) {
        return googleMapsClient.get()
                .uri(u -> u.path("/streetview/metadata")
                        .queryParam("location", lat + "," + lng)
                        .queryParam("radius", 5000)
                        .queryParam("source", "outdoor")
                        .queryParam("key", apiKey).build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(n -> n.path("status").asText())
                .block();
    }

    public List<String> getStatuses(List<Coordinate> coords) {
        log.info(">>> getStatuses: fetching metadata for {} coords", coords.size());

        List<String> statuses = Flux.fromIterable(coords)
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .flatMap(c ->
                        googleMapsClient.get()
                                .uri(u -> u.path("/streetview/metadata")
                                        .queryParam("location", c.lat()+","+c.lng())
                                        .queryParam("radius", 5000)
                                        .queryParam("source", "outdoor")
                                        .queryParam("key", apiKey).build())
                                .retrieve()
                                .bodyToMono(JsonNode.class)
                                .map(n -> {
                                    String status = n.path("status").asText();
                                    log.info("[{}] metadata {} → {}",
                                            Thread.currentThread().getName(), c, status);
                                    return status;
                                })
                )
                .sequential()
                .collectList()
                .block();

        log.info("<<< getStatuses complete: {} statuses", statuses != null ? statuses.size() : "null");
        return statuses;
    }

}
