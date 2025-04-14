package ch.uzh.ifi.hase.soprafs25.util;

import java.util.*;

public class RandomColorUtil {

    private RandomColorUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static final List<String> COLOR_PALETTE = List.of(
        "#FF6B6B", // Red
        "#6BCB77", // Green
        "#4D96FF", // Blue
        "#FFD93D", // Yellow
        "#845EC2", // Purple
        "#FF9671", // Orange
        "#00C9A7", // Teal
        "#C34A36", // Brown
        "#FFC75F", // Light Orange
        "#008F7A" // Dark Teal
    );

    private static final Map<String, Set<String>> roomColorMap = new HashMap<>();

    public static String assignColor(String roomId) {
        roomColorMap.putIfAbsent(roomId, new HashSet<>());
        Set<String> usedColors = roomColorMap.get(roomId);

        for (String color : COLOR_PALETTE) {
            if (!usedColors.contains(color)) {
                usedColors.add(color);
                return color;
            }
        }
        throw new IllegalStateException("No available colors left in this room: " + roomId);
    }

    public static void releaseColor(String roomId, String color) {
        Set<String> usedColors = roomColorMap.get(roomId);
        if (usedColors != null) {
            usedColors.remove(color);
            if (usedColors.isEmpty()) {
                roomColorMap.remove(roomId);
            }
        }
    }

    public static void clearRoom(String roomId) {
        roomColorMap.remove(roomId);
    }
    
    public static List<String> getColorPalette() {
        return COLOR_PALETTE;
    }
}
