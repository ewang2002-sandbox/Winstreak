package xyz.achsdiscord.parse;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static xyz.achsdiscord.parse.LobbyNameParser.LISTED_NUMS_OFFSET;
import static xyz.achsdiscord.parse.util.ParserUtility.ParserUtil.*;

public class InGameNameParser implements INameParser {
    /**
     * All possible team colors for 3v3v3v3 and 4v4v4v4.
     */
    private enum TeamColors {
        BLUE,
        GREEN,
        RED,
        YELLOW
    }

    // TODO get other doubles team colors

    public static final Color BLUE_TEAM_COLOR = new Color(0x5555FF);
    public static final Color GREEN_TEAM_COLOR = new Color(0x55FF55);
    // red team color is the same as the color of
    // STORE.HYPIXEL.NET link at bottom of player list
    public static final Color RED_TEAM_COLOR = new Color(0xFF5555);
    public static final Color YELLOW_TEAM_COLOR = new Color(0xFFFF55);

    // this is the color that is used in
    // "You are playing on..."
    public static final Color YOU_ARE_PLAYING_ON_COLOR = new Color(85, 255, 255);

    // private general variables
    private BufferedImage _img;
    private int _width;

    // for control
    private boolean calledCropIfFullScreen = false;
    private boolean calledCropHeaderFooter = false;
    private boolean calledMakeBlkWtFunc = false;
    private boolean calledFixImgFunc = false;

    /**
     * Creates a new NameProcessor object with the specified path to the image.
     *
     * @param file The path to the image.
     * @throws IOException If the path is invalid.
     */
    public InGameNameParser(File file) throws IOException {
        this._img = ImageIO.read(file);
    }

    /**
     * Creates a new NameProcessor object with the specified URL.
     *
     * @param link The link to the image.
     * @throws IOException If the URL is invalid.
     */
    public InGameNameParser(URL link) throws IOException {
        this._img = ImageIO.read(link);
    }

    /**
     * If the screenshot provided is a screenshot that shows the entire Minecraft application, call this method first to crop the screenshot appropriately.
     * <p>Note that the person must take a screenshot of the player list in the blue sky.
     * <p>If you use a screenshot that shows the entire Minecraft application, you MUST run this method first.
     *
     * @return This object.
     * @throws InvalidImageException If the screenshot has no player list.
     */
    public InGameNameParser cropImageIfFullScreen() throws InvalidImageException {
        if (this.calledCropIfFullScreen) {
            return this;
        }

        this.calledCropIfFullScreen = true;

        // top to bottom, left to right
        // find the top left coordinates of the
        // player list box
        int topLeftX = -1;
        int topLeftY = -1;
        major:
        for (int y = 0; y < this._img.getHeight(); y++) {
            for (int x = 0; x < this._img.getWidth(); x++) {
                if (this._img.getRGB(x, y) == YOU_ARE_PLAYING_ON_COLOR.getRGB()) {
                    topLeftX = x;
                    topLeftY = y;
                    break major;
                }
            }
        }

        // right to left, bottom to top
        int bottomRightX = -1;
        int bottomRightY = -1;
        major:
        for (int x = this._img.getWidth() - LISTED_NUMS_OFFSET; x >= 0; x--) {
            for (int y = this._img.getHeight() - 1; y >= 0; y--) {
                if (this._img.getRGB(x, y) == STORE_HYPIXEL_NET_DARK_COLOR.getRGB()) {
                    bottomRightX = x;
                    bottomRightY = y;
                    break major;
                }
            }
        }

        if (topLeftX == -1 || bottomRightX == -1) {
            throw new InvalidImageException("Invalid image given. Either a player " +
                    "list wasn't detected or the \"background\" of the player list isn't " +
                    "just the sky. Make sure the image contains the player list and that " +
                    "the \"background\" of the player list is just the sky (no clouds).");
        }

        this._img = cropImage(
                this._img,
                topLeftX,
                topLeftY,
                bottomRightX - topLeftX,
                bottomRightY - topLeftY
        );

        return this;
    }


    /**
     * Makes everything but the team colors white. This will also get the width of each character, which will be used later.
     * <p> You must call this method, regardless of screenshot type.
     * <p> If the screenshot provided shows all of Minecraft, you must run {@code adjustColorsAndIdentifyWidth()} first.
     *
     * @return This object.
     */
    public InGameNameParser adjustColorsAndIdentifyWidth() {
        if (this.calledMakeBlkWtFunc) {
            return this;
        }

        this.calledMakeBlkWtFunc = true;

        boolean foundLineWithValidColor = false;
        boolean hasTestedWidth = false;
        List<Integer> possibleWidths = new ArrayList<>();

        // make everything white except for the team colors
        for (int y = 0; y < this._img.getHeight(); y++) {
            int width = 0;
            for (int x = 0; x < this._img.getWidth(); x++) {
                Color color = new Color(this._img.getRGB(x, y));

                if (this.isValidColor(color)) {
                    foundLineWithValidColor = true;
                } else {
                    this._img.setRGB(x, y, Color.white.getRGB());
                }

                if (!hasTestedWidth) {
                    if (this.isValidColor(color)) {
                        foundLineWithValidColor = true;
                        ++width;
                    } else {
                        if (width != 0) {
                            possibleWidths.add(width);
                            width = 0;
                        }
                    }
                }

            }

            if (foundLineWithValidColor) {
                hasTestedWidth = true;
            }
        }

        // this should never be size 0
        if (possibleWidths.size() != 0) {
            this._width = mostCommon(possibleWidths);
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
        return this;
    }

    /**
     * Use this method if you need to crop out the header and footer of the player list.
     * <p>In the case of Hypixel, that will be "You are playing..." and "Ranks, Boosters..."
     * <p>Only run this method if the screenshot you provided was a screenshot of the entire Minecraft application OR you have both header and footer.
     * <p>You must have used the {@code adjustColorsAndIdentifyWidth()} method first.
     *
     * @return This object.
     * @throws InvalidImageException If the image wasn't processed through the {@code adjustColorsAndIdentifyWidth()} method.
     */
    public InGameNameParser cropHeaderAndFooter() throws InvalidImageException {
        if (this.calledCropHeaderFooter) {
            return this;
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
                    "run the adjustColorsAndIdentifyWidth() method first!");
        }

        this._img = cropImage(this._img, 0, topY, this._img.getWidth(), this._img.getHeight() - topY);
        return this;
    }

    /**
     * Attempts to crop the image so ONLY the player names show up. The picture must have processed.
     * <p>You must call this method.
     *
     * @return The object.
     * @throws InvalidImageException If the image wasn't processed through the {@code adjustColorsAndIdentifyWidth()} method.
     */
    public InGameNameParser fixImage() throws InvalidImageException {
        if (this.calledFixImgFunc) {
            return this;
        }

        this.calledFixImgFunc = true;
        // try to crop the
        // list of players
        int startingXVal;
        int startingYVal;
        int minStartingXVal = this._img.getWidth();
        int minStartingYVal = this._img.getHeight();
        // left to right, top to bottom
        for (int y = 0; y < this._img.getHeight(); y++) {
            for (int x = 0; x < this._img.getWidth(); x++) {
                if (this.isValidColor(new Color(this._img.getRGB(x, y)))) {
                    if (x < minStartingXVal) {
                        minStartingXVal = x;
                    }

                    if (y < minStartingYVal) {
                        minStartingYVal = y;
                    }
                }
            }
        }

        startingXVal = minStartingXVal;
        startingYVal = minStartingYVal;

        if (startingXVal == this._img.getWidth() || startingYVal == this._img.getHeight()) {
            throw new InvalidImageException("Couldn't crop the image. Make " +
                    "sure the image was processed beforehand; perhaps try to " +
                    "run the adjustColorsAndIdentifyWidth() method first!");
        }

        // make new copy of the image
        this._img = cropImage(this._img, startingXVal, startingYVal, this._img.getWidth() - startingXVal, this._img.getHeight() - startingYVal);
        // now we need to determine where to start

        // let's remove any blanks
        int y = 0;
        for (; y < this._img.getHeight(); y++) {
            if (this.isValidColor(new Color(this._img.getRGB(0, y)))
                && this.isValidColor(new Color(this._img.getRGB(this._width, y)))) {
                break;
            }
        }

        // now let's try again
        // but this time we're going to look
        // for the separator between the B/R/G/Y and the names of teammates
        boolean foundYSep = false;
        int x = 0;
        for (; x < this._img.getWidth(); x++) {
            int numberParticles = this.numberParticlesInVertLine(x);
            if (numberParticles == 0 && !foundYSep) {
                foundYSep = true;
            }

            if (foundYSep) {
                if (numberParticles == 0) {
                    continue;
                }

                break;
            }
        }
        // now we need to determine where to start

        // make another copy
        this._img = cropImage(this._img, x, y, this._img.getWidth() - x, this._img.getHeight() - y);

        // we're going to crop one more time
        // but this time, we're going to look
        return this;
    }

    @Override
    public List<String> getPlayerNames() {
        return this.getPlayerNames(new ArrayList<>());
    }

    /**
     * Gets the player names. If you do not call the appropriate methods, this method will return an empty list.
     *
     * @param peopleToExclude The people to exclude in the final list.
     * @return A list of names that were in the screenshot.
     */
    public List<String> getPlayerNames(List<String> peopleToExclude) {
        if (!this.calledMakeBlkWtFunc && !this.calledFixImgFunc) {
            return new ArrayList<>();
        }
        // will contain list of names
        List<String> names = new ArrayList<>();
        int y = 0;

        while (y <= this._img.getHeight()) {
            StringBuilder name = new StringBuilder();
            int x = 0;

            while (true) {
                StringBuilder ttlBytes = new StringBuilder();
                boolean errored = false;
                while (ttlBytes.length() == 0 || !ttlBytes.substring(ttlBytes.length() - 8).equals("00000000")) {
                    try {
                        StringBuilder columnBytes = new StringBuilder();
                        for (int dy = 0; dy < 8 * this._width; dy += this._width) {
                            if (this._img.getRGB(x, y + dy) == Color.black.getRGB()) {
                                columnBytes.append("1");
                            } else {
                                columnBytes.append("0");
                            }
                        }

                        ttlBytes.append(columnBytes.toString());
                        x += this._width;
                    } catch (Exception e) {
                        // this is probably due to poor cropping
                        // or any extra useless characters.
                        errored = true;
                        break;
                    }
                }

                if (!errored) {
                    ttlBytes = new StringBuilder(ttlBytes.substring(0, ttlBytes.length() - 8));
                }

                if (LobbyNameParser.binaryToCharacters.containsKey(ttlBytes.toString())) {
                    name.append(LobbyNameParser.binaryToCharacters.get(ttlBytes.toString()));
                } else {
                    break;
                }
            }
            if (!peopleToExclude.contains(name.toString())) {
                names.add(name.toString());
            }
            // 8 + 1 means the names + the space
            // that separates the first name from
            // the next one
            y += 9 * this._width;
        }

        names = names.stream()
                .filter(name -> name.length() != 0)
                .collect(Collectors.toList());
        return names;
    }

    /**
     * Determines if a pixel is a valid color (one of the rank colors).
     *
     * @param color The color.
     * @return Whether the color is valid or not.
     */
    private boolean isValidColor(Color color) {
        return color.getRGB() == RED_TEAM_COLOR.getRGB()
                || color.getRGB() == BLUE_TEAM_COLOR.getRGB()
                || color.getRGB() == YELLOW_TEAM_COLOR.getRGB()
                || color.getRGB() == GREEN_TEAM_COLOR.getRGB();
    }

    /**
     * Determines the number of particles in a line.
     *
     * @param y The y-line to check.
     * @return The number of particles in that line. "0" means there are no lines (i.e. a separator).
     */
    private int numberParticlesInHorizLine(final int y) {
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
    private int numberParticlesInVertLine(final int x) {
        int particles = 0;
        for (int y = 0; y < this._img.getHeight(); y++) {
            if (this.isValidColor(new Color(this._img.getRGB(x, y)))) {
                particles++;
            }
        }

        return particles;
    }

    public BufferedImage getImage() {
        return this._img;
    }
}
