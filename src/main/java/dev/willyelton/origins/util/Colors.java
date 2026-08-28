package dev.willyelton.origins.util;

public class Colors {
    public static int fromRGB(int red, int green, int blue) {
        return fromRGB(red, green, blue, 255);
    }

    public static int fromRGB(int red, int green, int blue, int alpha) {
        return blue + (green << 8) + (red << 16) + (alpha << 24);
    }

    public static int addAlpha(int color, int alpha) {
        int existingAlpha = color >> 24;
        if (existingAlpha != -1) return color;
        return color + (alpha << 24);
    }

    public static int[] toComponents(int color) {
        int a = color >> 24;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        return new int[] {a, r, g, b};
    }

    public static int average(int color1, int color2) {
        int[] components1 = toComponents(color1);
        int[] components2 = toComponents(color2);

        int a = (int) Math.sqrt(Math.pow(components1[0], 2) + Math.pow(components2[0], 2));
        int r = (int) Math.sqrt(Math.pow(components1[1], 2) + Math.pow(components2[1], 2));
        int g = (int) Math.sqrt(Math.pow(components1[2], 2) + Math.pow(components2[2], 2));
        int b = (int) Math.sqrt(Math.pow(components1[3], 2) + Math.pow(components2[3], 2));

        return fromRGB(r, g, b, a);
    }
}
