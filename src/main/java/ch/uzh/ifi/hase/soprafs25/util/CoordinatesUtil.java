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

@SuppressWarnings("squid:S1192") // suppress duplicate string literal warnings for coordinate keys
public class CoordinatesUtil {

    private static final String COORDINATES_FILE = "/coordinates.json";
    private static final String LOCATIONS_KEY    = "locations";
    private static final String MIN_LAT          = "minLat";
    private static final String MAX_LAT          = "maxLat";
    private static final String MIN_LNG          = "minLng";
    private static final String MAX_LNG          = "maxLng";

    private static final JsonNode root;
    private static final Random random = new Random();  // NOSONAR

    private CoordinatesUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    static {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = CoordinatesUtil.class.getResourceAsStream(COORDINATES_FILE)) {
            root = mapper.readTree(is);
        } catch (Exception e) {
            throw new CoordinatesLoadingException(e);
        }
    }

    private static JsonNode getRegionNode(String location) {
        JsonNode locationsNode = root.path(LOCATIONS_KEY);
        if (locationsNode.isMissingNode()) {
            throw new CoordinatesLoadingException(
                    new IllegalArgumentException("Missing '" + LOCATIONS_KEY + "' key in JSON"));
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
        } else if (regionNode.isObject()) {
            return regionNode;
        } else {
            throw new CoordinatesLoadingException(
                    new IllegalArgumentException("Unexpected JSON type for region"));
        }
    }

    public static Map<String, Double> getBoundingBox(String location) {
        JsonNode box = pickBoxNode(getRegionNode(location));

        double minLat = box.path(MIN_LAT).asDouble(Double.NaN);
        double maxLat = box.path(MAX_LAT).asDouble(Double.NaN);
        double minLng = box.path(MIN_LNG).asDouble(Double.NaN);
        double maxLng = box.path(MAX_LNG).asDouble(Double.NaN);

        if (Double.isNaN(minLat) || Double.isNaN(maxLat)
                || Double.isNaN(minLng) || Double.isNaN(maxLng)) {
            throw new CoordinatesLoadingException(
                    new IllegalArgumentException("Missing coordinate bounds in box"));
        }

        return Map.of(
                MIN_LAT, minLat,
                MAX_LAT, maxLat,
                MIN_LNG, minLng,
                MAX_LNG, maxLng
        );
    }

    public static Map<String, Double> getRandomCoordinate(String location) {
        Map<String, Double> box = getBoundingBox(location);
        double lat = randomInRange(box.get(MIN_LAT), box.get(MAX_LAT));
        double lng = randomInRange(box.get(MIN_LNG), box.get(MAX_LNG));
        return Map.of("lat", lat, "lng", lng);
    }

    public static List<Coordinate> getRandomCoordinates(String location, int count) {
        Map<String, Double> box = getBoundingBox(location);
        return IntStream.range(0, count)
                .mapToObj(i -> new Coordinate(
                        randomInRange(box.get(MIN_LAT), box.get(MAX_LAT)),
                        randomInRange(box.get(MIN_LNG), box.get(MAX_LNG))
                ))
                .toList();
    }

    private static double randomInRange(double min, double max) {
        double val = min + (max - min) * random.nextDouble();
        return Math.round(val * 1_000_000d) / 1_000_000d;
    }
}
