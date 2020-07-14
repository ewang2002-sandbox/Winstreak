package xyz.achsdiscord.parse.v2;

import xyz.achsdiscord.parse.Constants;
import xyz.achsdiscord.parse.exception.InvalidImageException;

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
 * <p>
 * It should be noted that this class is essentially the same as the one found
 * in v1. However, this particular method uses different version of getRGB.
 * </p>
 */
public abstract class AbstractNameParser {
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
     * valid colors, which is defined in {@link AbstractNameParser#isValidColor(Color)},
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
                Color color = new Color(this.getRGB(x, y));

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
                    if (this.isValidColor(new Color(this.getRGB(x, y)))) {
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
                    if (this.getRGB(x, y) != Color.white.getRGB()) {
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
                Color color = new Color(this.getRGB(x, y));
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
        } else {
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
            if (this.isValidColor(new Color(this.getRGB(x, y)))) {
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
            if (this.isValidColor(new Color(this.getRGB(x, y)))) {
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
                if (image.getRGB(x, y) == Constants.BOSS_BAR_COLOR.getRGB()) {
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

    /**
     * Gets the RGB at a specified pixel.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return The RGB value.
     */
    public int getRGB(int x, int y) {
        int[] pixel = this._img
                .getRaster()
                .getPixel(x, y, new int[3]);
        return new Color(pixel[0], pixel[1], pixel[2]).getRGB();
    }
}