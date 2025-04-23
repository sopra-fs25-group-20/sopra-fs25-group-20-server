package ch.uzh.ifi.hase.soprafs25.util;

import ch.uzh.ifi.hase.soprafs25.exceptions.CoordinatesLoadingException;
import ch.uzh.ifi.hase.soprafs25.service.image.Coordinate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CoordinatesUtilTest {

    private static Map<String, Map<String, Double>> locations;

    @BeforeAll
    @SuppressWarnings("unchecked")
    static void loadLocations() throws Exception {
        Field coordsField =
                CoordinatesUtil.class.getDeclaredField("coordinates");
        coordsField.setAccessible(true);
        Map<String, Map<String, Map<String, Double>>> coords =
                (Map<String, Map<String, Map<String, Double>>>) coordsField.get(null);
        locations = coords.get("locations");
        assertNotNull(locations, "The JSON must contain a 'locations' key");
        assertFalse(locations.isEmpty(), "Need at least one location to test");
    }

    @Test
    void constructor_throwsUnsupportedOperationException() throws Exception {
        Constructor<CoordinatesUtil> ctor =
                CoordinatesUtil.class.getDeclaredConstructor();
        ctor.setAccessible(true);

        InvocationTargetException ex =
                assertThrows(InvocationTargetException.class, ctor::newInstance);
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
    void getBoundingBox_validRegion_returnsExactBox() {
        // pick any one region from the loaded map
        String region = locations.keySet().iterator().next();
        Map<String, Double> expectedBox = locations.get(region);

        Map<String, Double> box = CoordinatesUtil.getBoundingBox(region);
        assertEquals(expectedBox, box);
        Set<String> keys = box.keySet();
        assertTrue(keys.containsAll(Set.of("minLat","maxLat","minLng","maxLng")));
    }

    @Test
    void getRandomCoordinate_withinBoundingBox() {
        String region = locations.keySet().iterator().next();
        Map<String, Double> box = CoordinatesUtil.getBoundingBox(region);

        Map<String, Double> coord = CoordinatesUtil.getRandomCoordinate(region);
        double lat = coord.get("lat");
        double lng = coord.get("lng");

        assertTrue(lat >= box.get("minLat") && lat <= box.get("maxLat"));
        assertTrue(lng >= box.get("minLng") && lng <= box.get("maxLng"));
    }

    @Test
    void getRandomCoordinates_sizeAndBounds() {
        String region = locations.keySet().iterator().next();
        Map<String, Double> box = CoordinatesUtil.getBoundingBox(region);

        int count = 5;
        List<Coordinate> list = CoordinatesUtil.getRandomCoordinates(region, count);
        assertEquals(count, list.size());

        list.forEach(c -> {
            assertTrue(c.lat() >= box.get("minLat") && c.lat() <= box.get("maxLat"));
            assertTrue(c.lng() >= box.get("minLng") && c.lng() <= box.get("maxLng"));
        });
    }
}
