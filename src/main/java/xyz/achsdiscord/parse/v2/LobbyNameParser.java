package xyz.achsdiscord.parse.v2;

import xyz.achsdiscord.parse.Constants;
import xyz.achsdiscord.parse.exception.InvalidImageException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LobbyNameParser extends AbstractNameParser {
    /**
     * Creates a new NameProcessor object with the specified BufferedImage.
     *
     * @param img The image.
     */
    public LobbyNameParser(BufferedImage img) {
        super(img);
    }

    /**
     * Creates a new NameProcessor object with the specified path to the image.
     *
     * @param file The path to the image.
     * @throws IOException If the path is invalid.
     */
    public LobbyNameParser(File file) throws IOException {
        super(ImageIO.read(file));
    }

    /**
     * Creates a new NameProcessor object with the specified URL.
     *
     * @param link The link to the image.
     * @throws IOException If the URL is invalid.
     */
    public LobbyNameParser(URL link) throws IOException {
        super(link);
    }

    /**
     * This implementation of {@code cropImageIfFullScreen()} will look for two major
     * details.
     * <ul>
     *     <li>
     *         The purple boss bar that is seen in any lobby or queue.
     *     </li>
     *     <li>
     *         The red "store.hypixel.link URL that is seen in the player list.
     *     </li>
     * </ul>
     * 
     * @see AbstractNameParser#cropImageIfFullScreen()
     * @throws InvalidImageException If the image does not meet the criteria specified
     * above.
     */
    @Override
    public void cropImageIfFullScreen() throws InvalidImageException {
        if (super.calledCropIfFullScreen) {
            return;
        }

        super.calledCropIfFullScreen = true;

        // top to bottom, left to right
        // find the top left coordinates of the
        // player list box
        int topLeftX = -1;
        int topLeftY = -1;
        major:
        for (int y = 0; y < super._img.getHeight(); y++) {
            for (int x = 0; x < super._img.getWidth(); x++) {
                if (super.getRGB(x, y) == Constants.BOSS_BAR_COLOR.getRGB()) {
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
        for (int x = super._img.getWidth() - Constants.LISTED_NUMS_OFFSET; x >= 0; x--) {
            for (int y = super._img.getHeight() - 1; y >= 0; y--) {
                if (super.getRGB(x, y) == Constants.STORE_HYPIXEL_NET_DARK_COLOR.getRGB()) {
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

        super.cropImage(
                topLeftX,
                topLeftY,
                bottomRightX - topLeftX,
                bottomRightY - topLeftY
        );
    }

    /**
     * This implementation of {@code fixImage()} simply crops the image
     * so the top-left image will be the start of the first name. In
     * other words, the top-left part of the image, representing (0, 0),
     * will be the start of the first name in the screenshot.
     * 
     * @see AbstractNameParser#fixImage()
     * @throws InvalidImageException If the image does not have any names.
     */
    @Override
    public void fixImage() throws InvalidImageException {
        if (super.calledFixImgFunc) {
            return;
        }

        super.calledFixImgFunc = true;
        // try to crop the
        // list of players
        int startingXVal;
        int startingYVal;
        int minStartingXVal = super._img.getWidth();
        int minStartingYVal = super._img.getHeight();
        // left to right, top to bottom
        for (int y = 0; y < super._img.getHeight(); y++) {
            for (int x = 0; x < super._img.getWidth(); x++) {
                if (this.isValidColor(new Color(super.getRGB(x, y)))) {
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

        if (startingXVal == super._img.getWidth() || startingYVal == super._img.getHeight()) {
            throw new InvalidImageException("Couldn't crop the image. Make " +
                    "sure the image was processed beforehand; perhaps try to " +
                    "run the adjustColors() method first!");
        }

        // make new copy of the image
        super.cropImage(startingXVal, startingYVal, super._img.getWidth() - startingXVal, super._img.getHeight() - startingYVal);
    }

    /**
     * This method will attempt to parse the given screenshot for
     * any names. If the appropriate methods were not called
     * beforehand, this method will return an empty list. 
     *
     * @return A list of names that were found in the screenshot.
     * @see AbstractNameParser#getPlayerNames()
     */
    @Override
    public List<String> getPlayerNames() {
        return this.getPlayerNames(new ArrayList<>());
    }

    /**
     * Gets the player names. If you do not call the appropriate methods, 
     * this method will return an empty list.
     *
     * @param peopleToExclude The people to exclude in the final list.
     * @return A list of names that were in the screenshot.
     * @see AbstractNameParser#getPlayerNames(List)
     */
    @Override
    public List<String> getPlayerNames(List<String> peopleToExclude) {
        if (!super.calledMakeBlkWtFunc && !super.calledFixImgFunc) {
            return new ArrayList<>();
        }
        // will contain list of names
        List<String> names = new ArrayList<>();
        int y = 0;

        while (y <= super._img.getHeight()) {
            StringBuilder name = new StringBuilder();
            int x = 0;

            while (true) {
                StringBuilder ttlBytes = new StringBuilder();
                boolean errored = false;
                while (ttlBytes.length() == 0 || !ttlBytes.substring(ttlBytes.length() - 8).equals("00000000")) {
                    try {
                        StringBuilder columnBytes = new StringBuilder();
                        for (int dy = 0; dy < 8 * super._width; dy += super._width) {
                            if (this.isValidColor(new Color(super.getRGB(x, y + dy)))) {
                                columnBytes.append("1");
                            } else {
                                columnBytes.append("0");
                            }
                        }

                        ttlBytes.append(columnBytes.toString());
                        x += super._width;
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

                if (Constants.binaryToCharactersMap.containsKey(ttlBytes.toString())) {
                    name.append(Constants.binaryToCharactersMap.get(ttlBytes.toString()));
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
            y += 9 * super._width;
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
     * @see AbstractNameParser#isValidColor(Color)
     */
    public boolean isValidColor(Color color) {
        return color.getRGB() == Constants.MVP_PLUS_PLUS.getRGB()
                || color.getRGB() == Constants.MVP_PLUS.getRGB()
                || color.getRGB() == Constants.MVP.getRGB()
                || color.getRGB() == Constants.VIP_PLUS.getRGB()
                || color.getRGB() == Constants.VIP.getRGB()
                || color.getRGB() == Constants.NONE.getRGB();
    }
}
