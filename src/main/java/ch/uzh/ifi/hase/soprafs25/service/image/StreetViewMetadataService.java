package ch.uzh.ifi.hase.soprafs25.service.image;

import ch.uzh.ifi.hase.soprafs25.exceptions.ImageLoadingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class StreetViewMetadataService {

    private static final Logger log = LoggerFactory.getLogger(StreetViewMetadataService.class);

    @Value("${google.maps.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getStatus(double lat, double lng) {
        String url = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("maps.googleapis.com")
                .path("/maps/api/streetview/metadata")
                .queryParam("location", lat + "," + lng)
                .queryParam("radius", 50000)
                .queryParam("key", apiKey)
                .toUriString();

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode rootNode = objectMapper.readTree(response);
            String status = rootNode.path("status").asText();

            log.info("STATUS: {} | Requested metadata URL: https://maps.googleapis.com/maps/api/streetview/metadata?location={},{}&radius=50000", status, lat, lng);

            return status;
        }
        catch (Exception e) {
            throw new ImageLoadingException(e);
        }
    }
}
