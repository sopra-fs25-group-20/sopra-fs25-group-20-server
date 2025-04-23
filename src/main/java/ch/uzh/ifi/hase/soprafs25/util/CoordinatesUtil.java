package ch.uzh.ifi.hase.soprafs25.util;

import ch.uzh.ifi.hase.soprafs25.exceptions.CoordinatesLoadingException;
import ch.uzh.ifi.hase.soprafs25.service.image.Coordinate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

public class CoordinatesUtil {

    private static final String COORDINATES_FILE = "/coordinates.json";
    private static final JsonNode root;
    private static final Random random = new Random();  // NOSONAR

    private CoordinatesUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    static {
        try (InputStream is = CoordinatesUtil.class.getResourceAsStream(COORDINATES_FILE)) {
            ObjectMapper mapper = new ObjectMapper();
            root = mapper.readTree(is);
        }
        catch (Exception e) {
            throw new CoordinatesLoadingException(e);
        }
    }

    private static JsonNode getRegionNode(String location) {
        JsonNode locationsNode = root.path("locations");
        if (locationsNode.isMissingNode()) {
            throw new CoordinatesLoadingException(
                    new IllegalArgumentException("Missing 'locations' key in JSON"));
        }
        JsonNode regionNode = locationsNode.path(location);
        if (regionNode.isMissingNode()) {
            throw new CoordinatesLoadingException(
                    new IllegalArgumentException("Invalid region: " + location));
        }
        return regionNode;
    }

    private static JsonNode pickBoxNode(JsonNode regionNode) {
        if (regionNode.isArray()) {
            int size = regionNode.size();
            if (size == 0) {
                throw new CoordinatesLoadingException(
                        new IllegalArgumentException("Empty box array for region"));
            }
            int idx = random.nextInt(size);
            return regionNode.get(idx);
        }
        else if (regionNode.isObject()) {
            return regionNode;
        }
        else {
            throw new CoordinatesLoadingException(
                    new IllegalArgumentException("Unexpected JSON type for region"));
        }
    }

    public static Map<String, Double> getBoundingBox(String location) {
        JsonNode regionNode = getRegionNode(location);
        JsonNode box = pickBoxNode(regionNode);

        double minLat = box.path("minLat").asDouble(Double.NaN);
        double maxLat = box.path("maxLat").asDouble(Double.NaN);
        double minLng = box.path("minLng").asDouble(Double.NaN);
        double maxLng = box.path("maxLng").asDouble(Double.NaN);

        if (Double.isNaN(minLat) || Double.isNaN(maxLat)
                || Double.isNaN(minLng) || Double.isNaN(maxLng)) {
            throw new CoordinatesLoadingException(
                    new IllegalArgumentException("Missing coordinate bounds in box"));
        }

        return Map.of(
                "minLat", minLat,
                "maxLat", maxLat,
                "minLng", minLng,
                "maxLng", maxLng
        );
    }

    public static Map<String, Double> getRandomCoordinate(String location) {
        Map<String, Double> box = getBoundingBox(location);
        double lat = randomInRange(box.get("minLat"), box.get("maxLat"));
        double lng = randomInRange(box.get("minLng"), box.get("maxLng"));
        return Map.of("lat", lat, "lng", lng);
    }

    public static List<Coordinate> getRandomCoordinates(String location, int count) {
        Map<String, Double> box = getBoundingBox(location);
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    double lat = randomInRange(box.get("minLat"), box.get("maxLat"));
                    double lng = randomInRange(box.get("minLng"), box.get("maxLng"));
                    return new Coordinate(lat, lng);
                })
                .toList();
    }

    private static double randomInRange(double min, double max) {
        double val = min + (max - min) * random.nextDouble();
        // altı basamak noktasal hassasiyet
        return Math.round(val * 1_000_000d) / 1_000_000d;
    }
}
