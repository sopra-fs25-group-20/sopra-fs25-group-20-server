package ch.uzh.ifi.hase.soprafs25.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Map;
import java.util.Random;

public class CoordinatesUtil {

    private static final String COORDINATES_FILE = "/coordinates.json";

    private static final Map<String, Map<String, Map<String, Double>>> coordinates;
    private static final Random random = new Random();

    static {
        try (InputStream is = CoordinatesUtil.class.getResourceAsStream(COORDINATES_FILE)) {
            ObjectMapper mapper = new ObjectMapper();
            coordinates = mapper.readValue(is, new TypeReference<>() {});
        }
        catch (Exception e) {
            throw new RuntimeException("Could not load coordinates.", e);
        }
    }

    public static Map<String, Double> getBoundingBox(String location) {
        return coordinates.get("locations").get(location.toLowerCase());
    }

    public static Map<String, Double> getRandomCoordinate(String location) {
        Map<String, Double> box = getBoundingBox(location);
        if (box == null) {
            return null;
        }
        double lat = randomInRange(box.get("minLat"), box.get("maxLat"));
        double lng = randomInRange(box.get("minLng"), box.get("maxLng"));
        return Map.of("lat", lat, "lng", lng);
    }

    private static double randomInRange(double min, double max) {
        double randomVal = min + (max - min) * random.nextDouble();
        return Math.round(randomVal * 1_000_000d) / 1_000_000d;
    }
}
