package xyz.achsdiscord.parse;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code AbstractNameParser}, along with its subclasses, is a class
 * that is primarily interested in parsing Minecraft characters from a
 * given Minecraft screenshot. This class has been specifically designed
 * for Hypixel Bedwars; however, with some modifications, this class can
 * be used for any purpose within Minecraft.
 * <p>
 * Unlike several other known implementations of this class and its
 * subclasses, this method is designed with portability and compatibility
 * in mind. This abstract class and its known subclasses will not use
 * any external libraries, and should work with almost any setup. Furthermore,
 * different GUI scales and screen resolutions have been tested and does,
 * indeed, work.
 * </p>
 * <p>
 * This class contains numerous static final variables with common colors
 * found in Hypixel and Bedwars. Furthermore, this class contains several
 * methods that may be useful in cropping an image.
 * </p>
 */
public abstract class AbstractNameParser {
    // rank colors
    public static final Color MVP_PLUS_PLUS = new Color(255, 170, 0);
    public static final Color MVP_PLUS = new Color(85, 255, 255);
    public static final Color MVP = new Color(85, 255, 255);
    public static final Color VIP_PLUS = new Color(85, 255, 85);
    public static final Color VIP = new Color(85, 255, 85);
    public static final Color NONE = new Color(170, 170, 170);

    // team colors

    /**
     * All possible team colors for 3v3v3v3 and 4v4v4v4.
     */
    public enum TeamColors {
        BLUE,
        GREEN,
        RED,
        YELLOW,
        UNKNOWN
    }

    // TODO get other doubles team colors

    public static final Color BLUE_TEAM_COLOR = new Color(0x5555FF);
    public static final Color GREEN_TEAM_COLOR = new Color(0x55FF55);
    // red team color is the same as the color of
    // STORE.HYPIXEL.NET link at bottom of player list
    public static final Color RED_TEAM_COLOR = new Color(0xFF5555);
    public static final Color YELLOW_TEAM_COLOR = new Color(0xFFFF55);

    // other colors

    // this is the color that is used in
    // "You are playing on..."
    public static final Color YOU_ARE_PLAYING_ON_COLOR = new Color(85, 255, 255);
    public static final Color STORE_HYPIXEL_NET_DARK_COLOR = new Color(0x3F1515);
    public static final Color BOSS_BAR_COLOR = new Color(0x76005C);

    public static final Map<String, String> binaryToCharactersMap;

    static {
        binaryToCharactersMap = new HashMap<>();
        binaryToCharactersMap.put("011111001111111010000010101000101011111010111100", "");
        binaryToCharactersMap.put("111111101111111010100000101000001111111001011110", "");
        binaryToCharactersMap.put("100000001100000001111110011111101100000010000000", "");
        binaryToCharactersMap.put("111111101111111010100010101000101111111001011100", "");
        binaryToCharactersMap.put("0111110010001010100100101010001001111100", "0");
        binaryToCharactersMap.put("0000001001000010111111100000001000000010", "1");
        binaryToCharactersMap.put("0100011010001010100100101001001001100110", "2");
        binaryToCharactersMap.put("0100010010000010100100101001001001101100", "3");
        binaryToCharactersMap.put("0001100000101000010010001000100011111110", "4");
        binaryToCharactersMap.put("1110010010100010101000101010001010011100", "5");
        binaryToCharactersMap.put("0011110001010010100100101001001000001100", "6");
        binaryToCharactersMap.put("1100000010000000100011101001000011100000", "7");
        binaryToCharactersMap.put("0110110010010010100100101001001001101100", "8");
        binaryToCharactersMap.put("0110000010010010100100101001010001111000", "9");
        binaryToCharactersMap.put("0000000100000001000000010000000100000001", "_");
        binaryToCharactersMap.put("0111111010100000101000001010000001111110", "A");
        binaryToCharactersMap.put("0000010000101010001010100010101000011110", "a");
        binaryToCharactersMap.put("1111111010100010101000101010001001011100", "B");
        binaryToCharactersMap.put("1111111000010010001000100010001000011100", "b");
        binaryToCharactersMap.put("0111110010000010100000101000001001000100", "C");
        binaryToCharactersMap.put("0001110000100010001000100010001000010100", "c");
        binaryToCharactersMap.put("1111111010000010100000101000001001111100", "D");
        binaryToCharactersMap.put("0001110000100010001000100001001011111110", "d");
        binaryToCharactersMap.put("1111111010100010101000101000001010000010", "E");
        binaryToCharactersMap.put("0001110000101010001010100010101000011010", "e");
        binaryToCharactersMap.put("1111111010100000101000001000000010000000", "F");
        binaryToCharactersMap.put("00100000011111101010000010100000", "f");
        binaryToCharactersMap.put("0111110010000010100000101010001010111100", "G");
        binaryToCharactersMap.put("0001100100100101001001010010010100111110", "g");
        binaryToCharactersMap.put("1111111000100000001000000010000011111110", "H");
        binaryToCharactersMap.put("1111111000010000001000000010000000011110", "h");
        binaryToCharactersMap.put("100000101111111010000010", "I");
        binaryToCharactersMap.put("10111110", "i");
        binaryToCharactersMap.put("0000010000000010000000100000001011111100", "J");
        binaryToCharactersMap.put("0000011000000001000000010000000110111110", "j");
        binaryToCharactersMap.put("1111111000100000001000000101000010001110", "K");
        binaryToCharactersMap.put("11111110000010000001010000100010", "k");
        binaryToCharactersMap.put("1111111000000010000000100000001000000010", "L");
        binaryToCharactersMap.put("1111110000000010", "l");
        binaryToCharactersMap.put("1111111001000000001000000100000011111110", "M");
        binaryToCharactersMap.put("0011111000100000000110000010000000011110", "m");
        binaryToCharactersMap.put("1111111001000000001000000001000011111110", "N");
        binaryToCharactersMap.put("0011111000100000001000000010000000011110", "n");
        binaryToCharactersMap.put("0111110010000010100000101000001001111100", "O");
        binaryToCharactersMap.put("0001110000100010001000100010001000011100", "o");
        binaryToCharactersMap.put("1111111010100000101000001010000001000000", "P");
        binaryToCharactersMap.put("0011111100010100001001000010010000011000", "p");
        binaryToCharactersMap.put("0111110010000010100000101000010001111010", "Q");
        binaryToCharactersMap.put("0001100000100100001001000001010000111111", "q");
        binaryToCharactersMap.put("1111111010100000101000001010000001011110", "R");
        binaryToCharactersMap.put("0011111000010000001000000010000000010000", "r");
        binaryToCharactersMap.put("0100010010100010101000101010001010011100", "S");
        binaryToCharactersMap.put("0001001000101010001010100010101000100100", "s");
        binaryToCharactersMap.put("1000000010000000111111101000000010000000", "T");
        binaryToCharactersMap.put("001000001111110000100010", "t");
        binaryToCharactersMap.put("1111110000000010000000100000001011111100", "U");
        binaryToCharactersMap.put("0011110000000010000000100000001000111110", "u");
        binaryToCharactersMap.put("1111000000001100000000100000110011110000", "V");
        binaryToCharactersMap.put("0011100000000100000000100000010000111000", "v");
        binaryToCharactersMap.put("1111111000000100000010000000010011111110", "W");
        binaryToCharactersMap.put("0011110000000010000011100000001000111110", "w");
        binaryToCharactersMap.put("1000111001010000001000000101000010001110", "X");
        binaryToCharactersMap.put("0010001000010100000010000001010000100010", "x");
        binaryToCharactersMap.put("1000000001000000001111100100000010000000", "Y");
        binaryToCharactersMap.put("0011100100000101000001010000010100111110", "y");
        binaryToCharactersMap.put("1000011010001010100100101010001011000010", "Z");
        binaryToCharactersMap.put("0010001000100110001010100011001000100010", "z");
    }


    public static final int LISTED_NUMS_OFFSET = 30;

    // private general variables
    protected BufferedImage _img;
    protected int _width;

    // for control
    protected boolean calledCropIfFullScreen = false;
    protected boolean calledCropHeaderFooter = false;
    protected boolean calledMakeBlkWtFunc = false;
    protected boolean calledFixImgFunc = false;

    /**
     * Creates a new AbstractNameParser object with the specified BufferedImage.
     *
     * @param img The image.
     */
    public AbstractNameParser(BufferedImage img) {
        this._img = img;
    }

    /**
     * Creates a new AbstractNameParser object with the specified path to the image.
     *
     * @param file The path to the image.
     * @throws IOException If the path is invalid.
     */
    public AbstractNameParser(File file) throws IOException {
        this._img = ImageIO.read(file);
    }

    /**
     * Creates a new AbstractNameParser object with the specified URL.
     *
     * @param link The link to the image.
     * @throws IOException If the URL is invalid.
     */
    public AbstractNameParser(URL link) throws IOException {
        this._img = ImageIO.read(link);
    }

    /**
     * The {@code cropImageIfFullScreen()} method is designed if the image
     * given in the constructor is a full-screen image (i.e. the screenshot
     * was taken using F2). This method attempts to crop the image so that
     * only the player list, along with a few other "noise," can be seen.
     *
     * @throws InvalidImageException If the screenshot has no player list.
     */
    public abstract void cropImageIfFullScreen() throws InvalidImageException;

    /**
     * This method, which will be used across all subclasses, is designed
     * to adjust the coloring of the image so anything other than the
     * valid colors, which is defined in {@link xyz.achsdiscord.parse.AbstractNameParser#isValidColor(Color)},
     * is made white.
     */
    public void adjustColors() {
        if (this.calledMakeBlkWtFunc) {
            return;
        }

        this.calledMakeBlkWtFunc = true;

        // replace any invalid colors
        // with white
        for (int y = 0; y < this._img.getHeight(); y++) {
            for (int x = 0; x < this._img.getWidth(); x++) {
                Color color = new Color(this._img.getRGB(x, y));

                if (!this.isValidColor(color)) {
                    this._img.setRGB(x, y, Color.white.getRGB());
                }
            }
        }

        // now, let's make sure there aren't any random "particles" sitting around.
        for (int x = 0; x < this._img.getWidth(); x++) {
            int numberOfParticles = numberParticlesInVertLine(x);
            // more than 10 particles means it's a name.
            if (numberOfParticles > 10) {
                break;
            } else {
                // probably leftovers from a skin with the same
                // colors as one of the rank colors
                for (int y = 0; y < this._img.getHeight(); y++) {
                    if (this.isValidColor(new Color(this._img.getRGB(x, y)))) {
                        this._img.setRGB(x, y, Color.white.getRGB());
                    }
                }
            }
        }
    }

    /**
     * Use this method if you need to crop out the header and footer of the player list.
     * <p>
     * In the case of Hypixel, that will be "You are playing..." and "Ranks, Boosters..."
     * </p>
     * <p>
     * Only run this method if the screenshot you provided was a screenshot of the entire Minecraft application OR you have both header and footer.
     * </p>
     * <p>
     * You must have used the {@code adjustColors()} method first.
     * </p>
     *
     * @throws InvalidImageException If the image wasn't processed through the {@code adjustColors()} method.
     */
    public void cropHeaderAndFooter() throws InvalidImageException {
        if (this.calledCropHeaderFooter) {
            return;
        }

        this.calledCropHeaderFooter = true;

        boolean topFirstBlankPast = false;
        boolean topSepFound = false;
        int topY = -1;

        // top to bottom
        for (int y = 0; y < this._img.getHeight(); y++) {
            boolean isSeparator = numberParticlesInHorizLine(y) == 0;
            // top first blank is the top separator that isn't cropped
            if (topFirstBlankPast) {
                // topSepFound is the separator that is
                // BELOW "You are playing on..."
                if (!topSepFound && isSeparator) {
                    topSepFound = true;
                }

                if (topSepFound) {
                    if (isSeparator) {
                        topY = y;
                    } else {
                        break;
                    }
                }
            } else {
                if (!isSeparator) {
                    topFirstBlankPast = true;
                }
            }
        }

        for (int y = this._img.getHeight() - 1; y >= 0; y--) {
            boolean isSep = numberParticlesInHorizLine(y) == 0;
            if (isSep) {
                break;
            } else {
                for (int x = 0; x < this._img.getWidth(); x++) {
                    if (this._img.getRGB(x, y) != Color.white.getRGB()) {
                        this._img.setRGB(x, y, Color.white.getRGB());
                    }
                }
            }
        }

        if (topY == -1) {
            throw new InvalidImageException("Couldn't crop the image. Make " +
                    "sure the image was processed beforehand; perhaps try to " +
                    "run the adjustColors() method first!");
        }

        this.cropImage(0, topY, this._img.getWidth(), this._img.getHeight() - topY);
    }

    /**
     * Attempts to crop the image so the player name appears at the top-left of the image.
     * The bottom and right side of an image may not be cropped solely so there is less
     * room for error when attempting to parse the names from the image.
     * <p>
     * This method must be called so the image is properly processed and ready to be parsed.
     * </p>
     *
     * @throws InvalidImageException If the image wasn't processed through the {@code #adjustColors()} method.
     */
    public abstract void fixImage() throws InvalidImageException;

    /**
     * This method attempts to find the width of each pixel. This is needed
     * in order to ensure accuracy of parsing.
     * <p>
     * You must have called {@code fixImage()} first.
     * </p>
     */
    public void identifyWidth() {
        Map<Integer, Integer> possibleWidths = new HashMap<>();
        int numWidthsProcessed = 0;

        for (int y = 0; y < this._img.getHeight(); y++) {
            int width = 0;
            for (int x = 0; x < this._img.getWidth(); x++) {
                Color color = new Color(this._img.getRGB(x, y));
                if (this.isValidColor(color)) {
                    ++width;
                } else {
                    if (width != 0) {
                        Integer val = possibleWidths.get(width);
                        possibleWidths.put(width, val == null ? 1 : val + 1);

                        numWidthsProcessed++;
                        width = 0;
                    }
                }
            }

            if (numWidthsProcessed > 200) {
                break;
            }
        }

        Map.Entry<Integer, Integer> max = null;

        for (Map.Entry<Integer, Integer> e : possibleWidths.entrySet()) {
            if (max == null || e.getValue() > max.getValue()) {
                max = e;
            }
        }

        if (max == null) {
            // default width
            this._width = 2;
        }
        else {
            this._width = max.getKey();
        }
    }

    /**
     * The {@code getPlayerName()} method will attempt to parse the screenshot
     * for any names. This particular method should be called if you do not
     * want to exclude any names from the parsing.
     *
     * @return An object that will somehow contain the names that were parsed.
     * Depending on the implementation, this method can either return a List
     * of names or a HashMap with the team colors as the key and the List of
     * names as the value of that key.
     */
    public abstract Object getPlayerNames();

    /**
     * The {@code getPlayerName(List<String>)} method will attempt to parse the screenshot
     * for any names. This particular method should be called if you have a list of names
     * that you want to exclude from the final parsing.
     *
     * @return An object that will somehow contain the names that were parsed.
     * Depending on the implementation, this method can either return a List
     * of names or a HashMap with the team colors as the key and the List of
     * names as the value of that key.
     */
    public abstract Object getPlayerNames(List<String> exempt);

    /**
     * This method will be implemented by its subclasses, and designates what one or
     * more valid color(s) is/are. The colors are used to parse the correct characters.
     * For this case, if a character (a letter/number/symbol) is a valid color, it will
     * be parsed.
     *
     * @param color The color to test.
     * @return Whether the color given in the parameter is a valid color.
     */
    protected abstract boolean isValidColor(Color color);

    /**
     * Determines the number of particles in a line.
     *
     * @param y The y-line to check.
     * @return The number of particles in that line. "0" means there are no lines (i.e. a separator).
     */
    public int numberParticlesInHorizLine(final int y) {
        int particles = 0;
        for (int x = 0; x < this._img.getWidth(); x++) {
            if (this.isValidColor(new Color(this._img.getRGB(x, y)))) {
                particles++;
            }
        }

        return particles;
    }

    /**
     * Determines the number of particles in a line.
     *
     * @param x The x-line to check.
     * @return The number of particles in that line. "0" means there are no lines (i.e. a separator).
     */
    public int numberParticlesInVertLine(final int x) {
        int particles = 0;
        for (int y = 0; y < this._img.getHeight(); y++) {
            if (this.isValidColor(new Color(this._img.getRGB(x, y)))) {
                particles++;
            }
        }

        return particles;
    }

    /**
     * Finds the most common element in a List.
     *
     * @param list The list.
     * @return The most common element.
     */
    public int mostCommon(List<Integer> list) {
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
     * @param x  The x-coordinate to start.
     * @param y  The y-coordinate to start.
     * @param dx How far (with respect to x) you want to crop in the x-direction.
     * @param dy How far (with respect to y) you want to crop in the y-direction.
     */
    public void cropImage(int x, int y, int dx, int dy) {
        BufferedImage img = this._img.getSubimage(x, y, dx, dy);
        BufferedImage copyOfImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = copyOfImage.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        this._img = copyOfImage;
    }

    /**
     * Returns the image.
     *
     * @return The image.
     */
    public BufferedImage getImage() {
        return this._img;
    }
}