package ch.uzh.ifi.hase.soprafs25.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RandomColorUtilTest {

    private final String testRoomId = "testRoom";

    @BeforeEach
    void setup() {
        RandomColorUtil.clearRoom(testRoomId);
    }

    @Test
    void constructor_throwsIllegalStateException() throws Exception {
        Constructor<RandomColorUtil> constructor = RandomColorUtil.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException ex = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertTrue(ex.getCause() instanceof IllegalStateException);
        assertEquals("Utility class", ex.getCause().getMessage());
    }

    @Test
    void assignColorReturnsUniqueColorUntilExhausted() {
        Set<String> assignedColors = new HashSet<>();

        for (int i = 0; i < RandomColorUtil.getColorPalette().size(); i++) {
            String color = RandomColorUtil.assignColor(testRoomId);
            assertNotNull(color);
            assertFalse(assignedColors.contains(color));
            assignedColors.add(color);
        }
        assertThrows(IllegalStateException.class, () -> RandomColorUtil.assignColor(testRoomId));
    }

    @Test
    void releaseColorMakesColorReusable() {
        String color1 = RandomColorUtil.assignColor(testRoomId);
        RandomColorUtil.releaseColor(testRoomId, color1);

        String color2 = RandomColorUtil.assignColor(testRoomId);
        assertEquals(color1, color2);
    }

    @Test
    void clearRoomReleasesAllColors() {
        RandomColorUtil.assignColor(testRoomId);
        RandomColorUtil.assignColor(testRoomId);
        RandomColorUtil.clearRoom(testRoomId);

        for (int i = 0; i < RandomColorUtil.getColorPalette().size(); i++) {
            assertDoesNotThrow(() -> RandomColorUtil.assignColor(testRoomId));
        }
    }

    @Test
    void getColorPaletteSize() {
        List<String> palette = RandomColorUtil.getColorPalette();
        assertEquals(10, palette.size());
    }
}