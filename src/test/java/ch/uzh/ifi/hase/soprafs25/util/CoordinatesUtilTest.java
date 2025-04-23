package ch.uzh.ifi.hase.soprafs25.util;

import ch.uzh.ifi.hase.soprafs25.exceptions.CoordinatesLoadingException;
import ch.uzh.ifi.hase.soprafs25.service.image.Coordinate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CoordinatesUtilTest {

    private static JsonNode locationsNode;

    @BeforeAll
    static void setup() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = CoordinatesUtil.class.getResourceAsStream("/coordinates.json")) {
            assertNotNull(is, "coordinates.json must be available on the classpath");
            JsonNode root = mapper.readTree(is);
            locationsNode = root.path("locations");
            assertTrue(locationsNode.isObject(), "The JSON must contain an object 'locations'");
            assertFalse(locationsNode.isEmpty(), "Need at least one location to test");
        }
    }

    @Test
    void constructor_throwsUnsupportedOperationException() throws Exception {
        Constructor<CoordinatesUtil> ctor = CoordinatesUtil.class.getDeclaredConstructor();
        ctor.setAccessible(true);

        InvocationTargetException ex = assertThrows(
                InvocationTargetException.class,
                ctor::newInstance
        );
        assertTrue(ex.getCause() instanceof UnsupportedOperationException);
        assertEquals("Utility class", ex.getCause().getMessage());
    }

    @Test
    void getBoundingBox_invalidRegion_throws() {
        CoordinatesLoadingException ex = assertThrows(
                CoordinatesLoadingException.class,
                () -> CoordinatesUtil.getBoundingBox("NOT_A_REGION")
        );
        assertEquals("Invalid region: NOT_A_REGION", ex.getErrorMessage());
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void getBoundingBox_validRegion_returnsOneOfDefinedBoxes() {
        String region = locationsNode.fieldNames().next();
        JsonNode regionNode = locationsNode.path(region);

        List<Map<String, Double>> expectedBoxes = new ArrayList<>();
        if (regionNode.isArray()) {
            regionNode.forEach(node -> expectedBoxes.add(Map.of(
                    "minLat", node.get("minLat").asDouble(),
                    "maxLat", node.get("maxLat").asDouble(),
                    "minLng", node.get("minLng").asDouble(),
                    "maxLng", node.get("maxLng").asDouble()
            )));
        } else {
            expectedBoxes.add(Map.of(
                    "minLat", regionNode.get("minLat").asDouble(),
                    "maxLat", regionNode.get("maxLat").asDouble(),
                    "minLng", regionNode.get("minLng").asDouble(),
                    "maxLng", regionNode.get("maxLng").asDouble()
            ));
        }

        Map<String, Double> box = CoordinatesUtil.getBoundingBox(region);
        assertTrue(
                expectedBoxes.contains(box),
                "Returned box should match one of defined boxes for region: " + region
        );
        assertTrue(
                box.keySet().containsAll(List.of("minLat", "maxLat", "minLng", "maxLng")),
                "Boundary keys must be present"
        );
    }

    @Test
    void getRandomCoordinate_withinAnyDefinedBox() {
        String region = locationsNode.fieldNames().next();
        JsonNode regionNode = locationsNode.path(region);

        List<Map<String, Double>> expectedBoxes = new ArrayList<>();
        if (regionNode.isArray()) {
            regionNode.forEach(node -> expectedBoxes.add(Map.of(
                    "minLat", node.get("minLat").asDouble(),
                    "maxLat", node.get("maxLat").asDouble(),
                    "minLng", node.get("minLng").asDouble(),
                    "maxLng", node.get("maxLng").asDouble()
            )));
        } else {
            expectedBoxes.add(Map.of(
                    "minLat", regionNode.get("minLat").asDouble(),
                    "maxLat", regionNode.get("maxLat").asDouble(),
                    "minLng", regionNode.get("minLng").asDouble(),
                    "maxLng", regionNode.get("maxLng").asDouble()
            ));
        }

        Map<String, Double> coord = CoordinatesUtil.getRandomCoordinate(region);
        double lat = coord.get("lat");
        double lng = coord.get("lng");

        boolean withinAny = expectedBoxes.stream().anyMatch(b ->
                lat >= b.get("minLat") && lat <= b.get("maxLat") &&
                        lng >= b.get("minLng") && lng <= b.get("maxLng")
        );
        assertTrue(
                withinAny,
                "Random coordinate should be within one of defined boxes for region: " + region
        );
    }

    @Test
    void getRandomCoordinates_sizeAndWithinBounds() {
        String region = locationsNode.fieldNames().next();
        JsonNode regionNode = locationsNode.path(region);

        List<Map<String, Double>> expectedBoxes = new ArrayList<>();
        if (regionNode.isArray()) {
            regionNode.forEach(node -> expectedBoxes.add(Map.of(
                    "minLat", node.get("minLat").asDouble(),
                    "maxLat", node.get("maxLat").asDouble(),
                    "minLng", node.get("minLng").asDouble(),
                    "maxLng", node.get("maxLng").asDouble()
            )));
        } else {
            expectedBoxes.add(Map.of(
                    "minLat", regionNode.get("minLat").asDouble(),
                    "maxLat", regionNode.get("maxLat").asDouble(),
                    "minLng", regionNode.get("minLng").asDouble(),
                    "maxLng", regionNode.get("maxLng").asDouble()
            ));
        }

        int count = 5;
        List<Coordinate> coords = CoordinatesUtil.getRandomCoordinates(region, count);
        assertEquals(count, coords.size(),
                "Should return the requested number of coordinates");

        for (Coordinate c : coords) {
            double lat = c.lat();
            double lng = c.lng();
            boolean withinAny = expectedBoxes.stream().anyMatch(b ->
                    lat >= b.get("minLat") && lat <= b.get("maxLat") &&
                            lng >= b.get("minLng") && lng <= b.get("maxLng")
            );
            assertTrue(
                    withinAny,
                    "Each random coordinate should be within one of defined boxes for region: " + region
            );
        }
    }
}