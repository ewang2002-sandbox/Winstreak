package xyz.achsdiscord.parse;

import xyz.achsdiscord.classes.RankColor;
import xyz.achsdiscord.util.Utility;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class NameProcessor {
    public static final RankColor[] rankColors = {
            new RankColor("MVP++", 255, 170, 0),
            new RankColor("MVP+", 85, 255, 255),
            new RankColor("MVP", 85, 255, 255),
            new RankColor("VIP+", 85, 255, 85),
            new RankColor("VIP", 85, 255, 85),
            new RankColor("NONE", 170, 170, 170)
    };

    private BufferedImage _img;

    /**
     * Creates a new NameProcessor object with the specified BufferedImage.
     * @param img The image.
     */
    public NameProcessor(BufferedImage img) {
        this._img = img;
    }

    /**
     * Creates a new NameProcessor object with the specified path to the image.
     * @param imgPath The path to the image.
     * @throws IOException If the path is invalid.
     */
    public NameProcessor(Path imgPath) throws IOException {
        this._img = ImageIO.read(imgPath.toFile());
    }

    /**
     * Creates a new NameProcessor object with the specified URL.
     * @param link The link to the image.
     * @throws IOException If the URL is invalid.
     */
    public NameProcessor(URL link) throws IOException {
        this._img = ImageIO.read(link);
    }

    /**
     * Fixes the image, essentially replacing every pixel with either white or black. This should be called first.
     * @return The object.
     */
    public NameProcessor fixImage() {
        for (int y = 0; y < this._img.getHeight(); y++) {
            for (int x = 0; x < this._img.getWidth(); x++) {
                Color color = new Color(this._img.getRGB(x, y));

                boolean isChanged = false;
                for (RankColor rankColor : rankColors) {
                    if (color.getRGB() == rankColor.color.getRGB()) {
                        isChanged = true;
                        this._img.setRGB(x, y, Color.black.getRGB());
                    }
                }

                if (!isChanged) {
                    this._img.setRGB(x, y, Color.white.getRGB());
                }
            }
        }

        return this;
    }

    /**
     * Crops the image so there is only text. This might screw up if there are other elements with the same color as the rank colors.
     * @return The object.
     * @throws InvalidImageException If the image is not valid (i.e. if the image given has no detectable colors).
     */
    public NameProcessor cropImage() throws InvalidImageException {
        int startingXVal;
        int startingYVal;
        int minStartingXVal = this._img.getWidth();
        int minStartingYVal = this._img.getHeight();
        // left to right, top to bottom
        for (int y = 0; y < this._img.getHeight(); y++) {
            for (int x = 0; x < this._img.getWidth(); x++) {
                if (this._img.getRGB(x, y) == Color.black.getRGB()) {
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
            throw new InvalidImageException("invalid image");
        }

        // right to left, top to bottom
        int finalXVal;
        int mostXVal = -1;
        for (int y = 0; y < this._img.getHeight(); y++) {
            for (int x = this._img.getWidth() - 1; x >= 0; x--) {
                if (this._img.getRGB(x, y) == Color.black.getRGB()) {
                    if (x > mostXVal) {
                        mostXVal = x;
                    }
                }
            }
        }

        finalXVal = this._img.getWidth() - mostXVal > 0
            ? ++mostXVal
            : mostXVal;

        // left to right, bottom to top
        int finalYVal;
        int mostYVal = -1;
        for (int x = 0; x < this._img.getWidth(); x++) {
            for (int y = this._img.getHeight() - 1; y >= 0; y--) {
                if (this._img.getRGB(x, y) == Color.black.getRGB()) {
                    if (y > mostYVal) {
                        mostYVal = y;
                    }
                }
            }
        }

        finalYVal = mostYVal;

        if (finalXVal == -1 || finalYVal == -1) {
            throw new InvalidImageException("invalid image given");
        }

        // make new copy of the image
        this._img = this.cropAndGetNewImage(this._img, startingXVal, startingYVal, finalXVal - startingXVal, finalYVal - startingYVal);
        return this;
    }

    /**
     * Gets the player names.
     * @return A list of names.
     */
    public List<String> getPlayerNames() {
        // will contain list of names
        final List<String> list = new ArrayList<>();

        // will contain cropped screenshots of each name
        final List<BufferedImage> allNamesInChunks = new ArrayList<>();

        // we're going to crop each image
        // for easier readability
        int origY = 0;
        boolean isInWhiteLineArea = true;
        do {
            int[] allYVals = new int[this._img.getWidth()];
            innerFor:
            for (int y = origY; y < this._img.getHeight(); y++) {
                for (int x = 0; x < this._img.getWidth(); x++) {
//                    System.out.println("(" + x + ", " + y + ")" + " -> " + this._img.getRGB(x, y) + " - " + isInWhiteLineArea);
                    if (this._img.getRGB(x, y) == Color.black.getRGB()) {
                        isInWhiteLineArea = false;
                    }

                    if (this._img.getRGB(x, y) == Color.white.getRGB()) {
                        allYVals[x] = y;
                    }

                    int uniqueElementCount = getUniqueElements(allYVals);

                    // this will be true if there is a whitespace
                    // has to be same x-vals for each element
                    if (uniqueElementCount == 1
                            // the difference between the x-val and the previous
                            // x-val is greater than 1
                            && allYVals[0] - origY > 1) {
                        if (isInWhiteLineArea) {
                            origY++;
                            continue;
                        }

                        isInWhiteLineArea = true;
                        break innerFor;
                    }
                }
            }

            // check for unique elements again
            int uniqueElementCount = getUniqueElements(allYVals);
            BufferedImage name = this.cropAndGetNewImage(
                    this._img,
                    0,
                    origY,
                    this._img.getWidth(),
                    uniqueElementCount == 1
                            ? allYVals[0] - origY
                            : this._img.getHeight() - origY
            );

            allNamesInChunks.add(name);
            origY = allYVals[0];

            // we're at the end of the image
        } while (origY != this._img.getHeight() - 1);

        // the fun begins. time to parse each image :)
        // image is a name
        for (BufferedImage image : allNamesInChunks) {
            // let's remove any excess blank space
            int finalXVal;
            int mostXVal = -1;
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = image.getWidth() - 1; x >= 0; x--) {
                    if (image.getRGB(x, y) == Color.black.getRGB()) {
                        if (x > mostXVal) {
                            mostXVal = x;
                        }
                    }
                }
            }

            // account for the fact that
            // the right side pixel is cut off
            finalXVal = image.getWidth() - mostXVal > 0
                    ? ++mostXVal
                    : mostXVal;
            if (finalXVal == -1) {
                continue;
            }

            BufferedImage newImage = this.cropAndGetNewImage(image, 0, 0, finalXVal, image.getHeight());
            // find each "divider"
            // a "divider" is a vertical white line.
            int origX = 0;
            isInWhiteLineArea = true;

            // characters will go in this array
            List<BufferedImage> characters = new ArrayList<>();
            // iterate over each character in the
            // picture
            do {
                int[] allXVals = new int[newImage.getHeight()];

                innerFor:
                for (int x = origX; x < newImage.getWidth(); x++) {
                    for (int y = 0; y < newImage.getHeight(); y++) {
                        if (newImage.getRGB(x, y) == Color.black.getRGB()) {
                            isInWhiteLineArea = false;
                        }

                        if (newImage.getRGB(x, y) == Color.white.getRGB()) {
                            allXVals[y] = x;
                        }

                        int uniqueElementCount = getUniqueElements(allXVals);

                        // this will be true if there is a whitespace
                        // has to be same x-vals for each element
                        if (uniqueElementCount == 1
                                // the difference between the x-val and the previous
                                // x-val is greater than 1
                                && allXVals[0] - origX > 1) {
                            if (isInWhiteLineArea) {
                                origX++;
                                continue;
                            }

                            isInWhiteLineArea = true;
                            break innerFor;
                        }
                    }
                }

                // check for unique elements again
                int uniqueElementCount = getUniqueElements(allXVals);
                BufferedImage character = this.cropAndGetNewImage(
                        newImage,
                        origX,
                        0,
                        uniqueElementCount == 1
                                ? allXVals[0] - origX
                                : newImage.getWidth() - origX,
                        newImage.getHeight()
                );

                characters.add(character);
                origX = allXVals[0];

                // we're at the end of the image
            } while (origX != newImage.getWidth() - 1);

            // now that each character has been cropped
            // we can begin the parsing process

        }

        return list;
    }

    public boolean hasTryHard(final int minBedsDestroyed, final int minFinalKills) {
        return false;
    }

    private void savePicturesDebug(List<BufferedImage> allNamesInChunks) throws IOException {
        int num = 0;
        for (BufferedImage img : allNamesInChunks) {
            File outputfile = new File("C:\\Users\\ewang\\Desktop\\Output\\" + ++num + ".png");
            ImageIO.write(img, "png", outputfile);
        }
    }

    private BufferedImage cropAndGetNewImage(BufferedImage originalImage, int x, int y, int dx, int dy) {
        //System.out.println(x + " | " + y + " | " + dx + " | " + dy);
        BufferedImage img = originalImage.getSubimage(x, y, dx, dy);
        BufferedImage copyOfImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = copyOfImage.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        return copyOfImage;
    }

    private int getUniqueElements(int[] items) {
        return (int) Arrays.stream(items).distinct().count();
    }


    public BufferedImage getImage() {
        return this._img;
    }
}

