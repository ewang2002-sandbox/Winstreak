package xyz.achsdiscord.parse;

import org.junit.Assert;
import xyz.achsdiscord.classes.RankColor;
import xyz.achsdiscord.util.Utility;

import javax.imageio.ImageIO;
import javax.naming.Name;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.FileNameMap;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class NameProcessor {
    public static final HashMap<String, BufferedImage> characterImageMap;

    public static final RankColor[] rankColors = {
            new RankColor("MVP++", 255, 170, 0),
            new RankColor("MVP+", 85, 255, 255),
            new RankColor("MVP", 85, 255, 255),
            new RankColor("VIP+", 85, 255, 85),
            new RankColor("VIP", 85, 255, 85),
            new RankColor("NONE", 170, 170, 170)
    };

    private BufferedImage _img;

    public NameProcessor(BufferedImage img) {
        this._img = img;
    }

    public NameProcessor(Path imgPath) throws IOException {
        this._img = ImageIO.read(imgPath.toFile());
    }

    public NameProcessor(URL link) throws IOException {
        this._img = ImageIO.read(link);
    }

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

    public NameProcessor cropImage() throws Exception {
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
            throw new Exception("invalid image");
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

        finalXVal = mostXVal;

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
            throw new Exception("invalid image given");
        }

        // make new copy of the image
        this._img = this.cropAndGetNewImage(this._img, startingXVal, startingYVal, finalXVal - startingXVal, finalYVal - startingYVal);
        return this;
    }

    public List<String> getPlayerNames() throws IOException {
        // will contain list of names
        final List<String> list = new ArrayList<>();

        // will contain cropped screenshots of each name
        final List<BufferedImage> allNamesInChunks = new ArrayList<>();

        // we're going to crop each image
        // for easier readability
        int origY = 0;
        boolean isInWhiteLineArea = false;
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
                            // x-val is greater than 1 (could get rid of above
                            // statement)
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

        this.savePicturesDebug(allNamesInChunks);

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

            // characters will go in this array
            List<BufferedImage> characters = new ArrayList<>();
            // iterate over each character in the
            // picture
            do {
                int[] allXVals = new int[newImage.getHeight()];

                innerFor:
                for (int x = origX; x < newImage.getWidth(); x++) {
                    for (int y = 0; y < newImage.getHeight(); y++) {
                        if (newImage.getRGB(x, y) == Color.white.getRGB()) {
                            allXVals[y] = x;
                        }

                        int uniqueElementCount = getUniqueElements(allXVals);

                        // this will be true if there is a whitespace
                        // has to be same x-vals for each element
                        if (uniqueElementCount == 1
                                // cannot be the same x-val as the previous line
                                && allXVals[0] != origX
                                // the difference between the x-val and the previous
                                // x-val is greater than 1 (could get rid of above
                                // statement)
                                && allXVals[0] - origX > 1) {
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

            // TODO...
        }
        return list;
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

    static {
        characterImageMap = new HashMap<>();
        try {
            characterImageMap.put("0", ImageIO.read(new File(Utility.getPathOfResource("mcText/0.png"))));
            characterImageMap.put("1", ImageIO.read(new File(Utility.getPathOfResource("mcText/1.png"))));
            characterImageMap.put("2", ImageIO.read(new File(Utility.getPathOfResource("mcText/2.png"))));
            characterImageMap.put("3", ImageIO.read(new File(Utility.getPathOfResource("mcText/3.png"))));
            characterImageMap.put("4", ImageIO.read(new File(Utility.getPathOfResource("mcText/4.png"))));
            characterImageMap.put("5", ImageIO.read(new File(Utility.getPathOfResource("mcText/5.png"))));
            characterImageMap.put("6", ImageIO.read(new File(Utility.getPathOfResource("mcText/6.png"))));
            characterImageMap.put("7", ImageIO.read(new File(Utility.getPathOfResource("mcText/7.png"))));
            characterImageMap.put("8", ImageIO.read(new File(Utility.getPathOfResource("mcText/8.png"))));
            characterImageMap.put("9", ImageIO.read(new File(Utility.getPathOfResource("mcText/9.png"))));
            characterImageMap.put("A", ImageIO.read(new File(Utility.getPathOfResource("mcText/a_cap.png"))));
            characterImageMap.put("a", ImageIO.read(new File(Utility.getPathOfResource("mcText/a_low.png"))));
            characterImageMap.put("B", ImageIO.read(new File(Utility.getPathOfResource("mcText/b_cap.png"))));
            characterImageMap.put("b", ImageIO.read(new File(Utility.getPathOfResource("mcText/b_low.png"))));
            characterImageMap.put("C", ImageIO.read(new File(Utility.getPathOfResource("mcText/c_cap.png"))));
            characterImageMap.put("c", ImageIO.read(new File(Utility.getPathOfResource("mcText/c_low.png"))));
            characterImageMap.put("D", ImageIO.read(new File(Utility.getPathOfResource("mcText/d_cap.png"))));
            characterImageMap.put("d", ImageIO.read(new File(Utility.getPathOfResource("mcText/d_low.png"))));
            characterImageMap.put("E", ImageIO.read(new File(Utility.getPathOfResource("mcText/e_cap.png"))));
            characterImageMap.put("e", ImageIO.read(new File(Utility.getPathOfResource("mcText/e_low.png"))));
            characterImageMap.put("F", ImageIO.read(new File(Utility.getPathOfResource("mcText/f_cap.png"))));
            characterImageMap.put("f", ImageIO.read(new File(Utility.getPathOfResource("mcText/f_low.png"))));
            characterImageMap.put("G", ImageIO.read(new File(Utility.getPathOfResource("mcText/g_cap.png"))));
            characterImageMap.put("g", ImageIO.read(new File(Utility.getPathOfResource("mcText/g_low.png"))));
            characterImageMap.put("H", ImageIO.read(new File(Utility.getPathOfResource("mcText/h_cap.png"))));
            characterImageMap.put("h", ImageIO.read(new File(Utility.getPathOfResource("mcText/h_low.png"))));
            characterImageMap.put("I", ImageIO.read(new File(Utility.getPathOfResource("mcText/i_cap.png"))));
            characterImageMap.put("i", ImageIO.read(new File(Utility.getPathOfResource("mcText/i_low.png"))));
            characterImageMap.put("J", ImageIO.read(new File(Utility.getPathOfResource("mcText/j_cap.png"))));
            characterImageMap.put("j", ImageIO.read(new File(Utility.getPathOfResource("mcText/j_low.png"))));
            characterImageMap.put("K", ImageIO.read(new File(Utility.getPathOfResource("mcText/k_cap.png"))));
            characterImageMap.put("k", ImageIO.read(new File(Utility.getPathOfResource("mcText/k_low.png"))));
            characterImageMap.put("L", ImageIO.read(new File(Utility.getPathOfResource("mcText/l_cap.png"))));
            characterImageMap.put("l", ImageIO.read(new File(Utility.getPathOfResource("mcText/l_low.png"))));
            characterImageMap.put("M", ImageIO.read(new File(Utility.getPathOfResource("mcText/m_cap.png"))));
            characterImageMap.put("m", ImageIO.read(new File(Utility.getPathOfResource("mcText/m_low.png"))));
            characterImageMap.put("N", ImageIO.read(new File(Utility.getPathOfResource("mcText/n_cap.png"))));
            characterImageMap.put("n", ImageIO.read(new File(Utility.getPathOfResource("mcText/n_low.png"))));
            characterImageMap.put("O", ImageIO.read(new File(Utility.getPathOfResource("mcText/o_cap.png"))));
            characterImageMap.put("o", ImageIO.read(new File(Utility.getPathOfResource("mcText/o_low.png"))));
            characterImageMap.put("P", ImageIO.read(new File(Utility.getPathOfResource("mcText/p_cap.png"))));
            characterImageMap.put("p", ImageIO.read(new File(Utility.getPathOfResource("mcText/p_low.png"))));
            characterImageMap.put("Q", ImageIO.read(new File(Utility.getPathOfResource("mcText/q_cap.png"))));
            characterImageMap.put("q", ImageIO.read(new File(Utility.getPathOfResource("mcText/q_low.png"))));
            characterImageMap.put("R", ImageIO.read(new File(Utility.getPathOfResource("mcText/r_cap.png"))));
            characterImageMap.put("r", ImageIO.read(new File(Utility.getPathOfResource("mcText/r_low.png"))));
            characterImageMap.put("S", ImageIO.read(new File(Utility.getPathOfResource("mcText/s_cap.png"))));
            characterImageMap.put("s", ImageIO.read(new File(Utility.getPathOfResource("mcText/s_low.png"))));
            characterImageMap.put("T", ImageIO.read(new File(Utility.getPathOfResource("mcText/t_cap.png"))));
            characterImageMap.put("t", ImageIO.read(new File(Utility.getPathOfResource("mcText/t_low.png"))));
            characterImageMap.put("U", ImageIO.read(new File(Utility.getPathOfResource("mcText/u_cap.png"))));
            characterImageMap.put("u", ImageIO.read(new File(Utility.getPathOfResource("mcText/u_low.png"))));
            characterImageMap.put("V", ImageIO.read(new File(Utility.getPathOfResource("mcText/v_cap.png"))));
            characterImageMap.put("v", ImageIO.read(new File(Utility.getPathOfResource("mcText/v_low.png"))));
            characterImageMap.put("W", ImageIO.read(new File(Utility.getPathOfResource("mcText/w_cap.png"))));
            characterImageMap.put("w", ImageIO.read(new File(Utility.getPathOfResource("mcText/w_low.png"))));
            characterImageMap.put("X", ImageIO.read(new File(Utility.getPathOfResource("mcText/x_cap.png"))));
            characterImageMap.put("x", ImageIO.read(new File(Utility.getPathOfResource("mcText/x_low.png"))));
            characterImageMap.put("Y", ImageIO.read(new File(Utility.getPathOfResource("mcText/y_cap.png"))));
            characterImageMap.put("y", ImageIO.read(new File(Utility.getPathOfResource("mcText/y_low.png"))));
            characterImageMap.put("Z", ImageIO.read(new File(Utility.getPathOfResource("mcText/z_cap.png"))));
            characterImageMap.put("z", ImageIO.read(new File(Utility.getPathOfResource("mcText/z_low.png"))));
            characterImageMap.put("_", ImageIO.read(new File(Utility.getPathOfResource("mcText/underscore.png"))));
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
}

