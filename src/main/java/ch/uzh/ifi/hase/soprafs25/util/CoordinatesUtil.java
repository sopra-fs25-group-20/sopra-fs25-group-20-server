package ch.uzh.ifi.hase.soprafs25.util;

import ch.uzh.ifi.hase.soprafs25.exceptions.CoordinatesLoadingException;
import ch.uzh.ifi.hase.soprafs25.service.image.Coordinate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        Map<String, Map<String, Double>> locations = coordinates.get("locations");
        Map<String, Double> box = locations.get(location);

        if (box == null || box.isEmpty()) {
            throw new CoordinatesLoadingException(new IllegalArgumentException("Invalid region: " + location));
        }

        return box;
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
                .collect(Collectors.toList());
    }

    private static double randomInRange(double min, double max) {
        double randomVal = min + (max - min) * secureRandom.nextDouble();
        return Math.round(randomVal * 1_000_000d) / 1_000_000d;
    }
}
