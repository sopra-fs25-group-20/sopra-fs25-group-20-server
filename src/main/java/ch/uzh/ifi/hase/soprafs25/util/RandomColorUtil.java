package ch.uzh.ifi.hase.soprafs25.util;

import java.util.*;

public class RandomColorUtil {

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

    public static String getAvailableColor(Set<String> usedColors) {
        for (String color : COLOR_PALETTE) {
            if (!usedColors.contains(color)) {
                return color;
            }
        }
        throw new IllegalStateException("No available colors left in this room.");
    }

    public static List<String> getColorPalette() {
        return COLOR_PALETTE;
    }
}
