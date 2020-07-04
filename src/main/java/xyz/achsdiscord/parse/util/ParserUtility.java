package xyz.achsdiscord.parse.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParserUtility {
    public static class ParserUtil {
        // other colors
        public static final Color STORE_HYPIXEL_NET_DARK_COLOR = new Color(0x3F1515);
        public static final Color BOSS_BAR_COLOR = new Color(0x76005C);

        /**
         * Finds the most common element in a List.
         *
         * @param list The list.
         * @return The most common element.
         */
        public static int mostCommon(List<Integer> list) {
            Map<Integer, Integer> map = new HashMap<>();

            for (Integer t : list) {
                Integer val = map.get(t);
                map.put(t, val == null ? 1 : val + 1);
            }

            Map.Entry<Integer, Integer> max = null;

            for (Map.Entry<Integer, Integer> e : map.entrySet()) {
                if (max == null || e.getValue() > max.getValue())
                    max = e;
            }

            if (max == null) {
                return -1;
            }

            return max.getKey();
        }

        /**
         * Determines whether the image was taken in a lobby (queue) or in-game. This only supports full-screen images at the moment.
         *
         * @param image The screenshot.
         * @return Whether the picture represents a picture taken in lobby or in-game.
         */
        public static boolean isInLobby(BufferedImage image) {
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    if (image.getRGB(x, y) == BOSS_BAR_COLOR.getRGB()) {
                        return true;
                    }
                }
            }

            return false;
        }

        /**
         * Crops the image.
         *
         * @param image The image.
         * @param x  The x-coordinate to start.
         * @param y  The y-coordinate to start.
         * @param dx How far (with respect to x) you want to crop in the x-direction.
         * @param dy How far (with respect to y) you want to crop in the y-direction.
         * @return The cropped image.
         */
        public static BufferedImage cropImage(BufferedImage image, int x, int y, int dx, int dy) {
            BufferedImage img = image.getSubimage(x, y, dx, dy);
            BufferedImage copyOfImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics g = copyOfImage.createGraphics();
            g.drawImage(img, 0, 0, null);
            g.dispose();
            return copyOfImage;
        }
    }
}
