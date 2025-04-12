package ch.uzh.ifi.hase.soprafs25.util;

import ch.uzh.ifi.hase.soprafs25.exceptions.CoordinatesLoadingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Map;

public class CoordinatesUtil {

    private static final String COORDINATES_FILE = "/coordinates.json";
    private static final Map<String, Map<String, Map<String, Double>>> coordinates;
    private static final SecureRandom secureRandom = new SecureRandom();

    private CoordinatesUtil() {
        throw new UnsupportedOperationException("Utility class");
    }


    static {
        try (InputStream is = CoordinatesUtil.class.getResourceAsStream(COORDINATES_FILE)) {
            ObjectMapper mapper = new ObjectMapper();
            coordinates = mapper.readValue(is, new TypeReference<>() {});
        }
        catch (Exception e) {
            throw new CoordinatesLoadingException(e);
        }
    }

    public static Map<String, Double> getBoundingBox(String location) {
        Map<String, Map<String, Double>> locationsMap = coordinates.get("locations");
        if (locationsMap == null) {
            return Collections.emptyMap();
        }
        Map<String, Double> box = locationsMap.get(location.toLowerCase());
        return (box != null) ? box : Collections.emptyMap();
    }


    public static Map<String, Double> getRandomCoordinate(String location) {
        Map<String, Double> box = getBoundingBox(location);
        if (box.isEmpty()) {
            return Collections.emptyMap();
        }
        double lat = randomInRange(box.get("minLat"), box.get("maxLat"));
        double lng = randomInRange(box.get("minLng"), box.get("maxLng"));
        return Map.of("lat", lat, "lng", lng);
    }

    private static double randomInRange(double min, double max) {
        double randomVal = min + (max - min) * secureRandom.nextDouble();
        return Math.round(randomVal * 1_000_000d) / 1_000_000d;
    }
}
