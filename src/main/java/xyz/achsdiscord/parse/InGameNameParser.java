package xyz.achsdiscord.parse;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InGameNameParser extends AbstractNameParser {
    /**
     * Creates a new InGameNameParser object with the specified path to the image.
     *
     * @param file The path to the image.
     * @throws IOException If the path is invalid.
     */
    public InGameNameParser(File file) throws IOException {
        super(file);
    }

    /**
     * Creates a new InGameNameParser object with the specified URL.
     *
     * @param link The link to the image.
     * @throws IOException If the URL is invalid.
     */
    public InGameNameParser(URL link) throws IOException {
        super(link);
    }


    /**
     * This implementation of {@code cropImageIfFullScreen()} will look for two major
     * details.
     * <ul>
     *     <li>
     *         The cyan (MVP+) color that clearly says "You are playing on..."
     *     </li>
     *     <li>
     *         The red "store.hypixel.link URL that is seen in the player list.
     *     </li>
     * </ul>
     *
     * @throws InvalidImageException If the image does not meet the criteria specified
     *                               above.
     * @see xyz.achsdiscord.parse.AbstractNameParser#cropImageIfFullScreen()
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
                if (super._img.getRGB(x, y) == YOU_ARE_PLAYING_ON_COLOR.getRGB()) {
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
        for (int x = super._img.getWidth() - LISTED_NUMS_OFFSET; x >= 0; x--) {
            for (int y = super._img.getHeight() - 1; y >= 0; y--) {
                if (super._img.getRGB(x, y) == STORE_HYPIXEL_NET_DARK_COLOR.getRGB()) {
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
     * This implementation of {@code fixImage()} will attempt to crop the
     * image twice. First, the method will attempt to crop out the team
     * colors (R/G/Y/B in bold). Then, the method will attempt to crop out
     * any remaining areas of the image so that the top-left corner
     * of the image (0,0) does, indeed, contain the first name.
     *
     * @throws InvalidImageException If the image does not have any names.
     * @see AbstractNameParser#fixImage()
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
                if (this.isValidColor(new Color(super._img.getRGB(x, y)))) {
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
        // now we need to determine where to start

        // let's remove any blanks
        int y = 0;
        for (; y < super._img.getHeight(); y++) {
            if (this.isValidColor(new Color(super._img.getRGB(0, y)))
                    && this.isValidColor(new Color(super._img.getRGB(super._width, y)))) {
                break;
            }
        }

        // now let's try again
        // but this time we're going to look
        // for the separator between the B/R/G/Y and the names of teammates
        boolean foundYSep = false;
        int x = 0;
        for (; x < super._img.getWidth(); x++) {
            int numberParticles = super.numberParticlesInVertLine(x);
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
        super.cropImage(x, y, super._img.getWidth() - x, super._img.getHeight() - y);
    }

    /**
     * This method will attempt to parse the given screenshot for
     * any names. If the appropriate methods were not called
     * beforehand, this method will return an empty HashMap.
     *
     * @return A HashMap with the team colors as the key and a
     * list of player names in that team as the value.
     * @see AbstractNameParser#getPlayerNames()
     */
    @Override
    public Map<TeamColors, List<String>> getPlayerNames() {
        return this.getPlayerNames(new ArrayList<>());
    }

    /**
     * Gets the player names. If you do not call the appropriate methods,
     * this method will return an empty HashMap.
     *
     * @param peopleToExclude The people to exclude in the final list.
     * @return A HashMap with the team colors as the key and a
     * list of player names in that team as the value.
     * @see AbstractNameParser#getPlayerNames(List)
     */
    @Override
    public Map<TeamColors, List<String>> getPlayerNames(List<String> peopleToExclude) {
        if (!super.calledMakeBlkWtFunc && !super.calledFixImgFunc) {
            return new HashMap<>();
        }
        // will contain list of names
        int y = 0;

        Map<TeamColors, List<String>> teammates = new HashMap<>();
        List<TeamColors> colorsToIgnore = new ArrayList<>();

        TeamColors currentColor = null;
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
                            Color color = new Color(super._img.getRGB(x, y + dy));
                            if (this.isValidColor(color)) {
                                currentColor = this.getTeamColor(color);
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

                if (LobbyNameParser.binaryToCharactersMap.containsKey(ttlBytes.toString())) {
                    name.append(LobbyNameParser.binaryToCharactersMap.get(ttlBytes.toString()));
                } else {
                    break;
                }
            }

            if (peopleToExclude.contains(name.toString())) {
                colorsToIgnore.add(currentColor);
            }

            if (!colorsToIgnore.contains(currentColor) && !name.toString().trim().equals("")) {
                if (currentColor == null) {
                    System.out.println("No Color: " + name.toString());
                    continue;
                }
                else {
                    if (!teammates.containsKey(currentColor)) {
                        teammates.put(currentColor, new ArrayList<>());
                    }
                    teammates.get(currentColor).add(name.toString());
                }
            }
            // 8 + 1 means the names + the space
            // that separates the first name from
            // the next one
            y += 9 * super._width;
        }

        return teammates;
    }

    /**
     * Gets the Team Color enum member from a given color.
     *
     * @param color The given color.
     * @return The TeamColor enum member corresponding to that color.
     */
    private TeamColors getTeamColor(Color color) {
        int rgb = color.getRGB();
        if (rgb == AbstractNameParser.BLUE_TEAM_COLOR.getRGB()) {
            return TeamColors.BLUE;
        } else if (rgb == AbstractNameParser.RED_TEAM_COLOR.getRGB()) {
            return TeamColors.RED;
        } else if (rgb == AbstractNameParser.YELLOW_TEAM_COLOR.getRGB()) {
            return TeamColors.YELLOW;
        } else if (rgb == AbstractNameParser.GREEN_TEAM_COLOR.getRGB()) {
            return TeamColors.GREEN;
        } else {
            return TeamColors.UNKNOWN;
        }
    }

    /**
     * Determines if a pixel is a valid color (one of the rank colors).
     *
     * @param color The color.
     * @return Whether the color is valid or not.
     */
    public boolean isValidColor(Color color) {
        return color.getRGB() == RED_TEAM_COLOR.getRGB()
                || color.getRGB() == BLUE_TEAM_COLOR.getRGB()
                || color.getRGB() == YELLOW_TEAM_COLOR.getRGB()
                || color.getRGB() == GREEN_TEAM_COLOR.getRGB();
    }
}
