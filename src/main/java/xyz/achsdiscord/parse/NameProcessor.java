package xyz.achsdiscord.parse;

import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public class NameProcessor {
    public static final Color MVPPlusPlus = new Color(255, 170, 0);
    public static final Color MVPPlus = new Color(85, 255, 255);
    public static final Color MVP = new Color(85, 255, 255);
    public static final Color VIPPlus = new Color(85, 255, 85);
    public static final Color VIP = new Color(85, 255, 85);
    public static final Color NONE = new Color(170, 170, 170);

    public static final Map<BigInteger, String> hashMap;

    static {
        hashMap = new HashMap<>();
        hashMap.put(new BigInteger("190"), "i");
        hashMap.put(new BigInteger("64514"), "l");
        hashMap.put(new BigInteger("2161698"), "t");
        hashMap.put(new BigInteger("8584834"), "I");
        hashMap.put(new BigInteger("545169568"), "f");
        hashMap.put(new BigInteger("4261942306"), "k");
        hashMap.put(new BigInteger("137432555241148"), "");
        hashMap.put(new BigInteger("280369570053726"), "");
        hashMap.put(new BigInteger("141564244312192"), "");
        hashMap.put(new BigInteger("280369603739228"), "");
        hashMap.put(new BigInteger("534900810364"), "0");
        hashMap.put(new BigInteger("9713877506"), "1");
        hashMap.put(new BigInteger("302972572262"), "2");
        hashMap.put(new BigInteger("294248419948"), "3");
        hashMap.put(new BigInteger("103755057406"), "4");
        hashMap.put(new BigInteger("981981110940"), "5");
        hashMap.put(new BigInteger("259083375116"), "6");
        hashMap.put(new BigInteger("826790547680"), "7");
        hashMap.put(new BigInteger("466315547244"), "8");
        hashMap.put(new BigInteger("414775940216"), "9");
        hashMap.put(new BigInteger("4311810305"), "_");
        hashMap.put(new BigInteger("543860760702"), "A");
        hashMap.put(new BigInteger("17887275550"), "a");
        hashMap.put(new BigInteger("1093650260572"), "B");
        hashMap.put(new BigInteger("1091225920028"), "b");
        hashMap.put(new BigInteger("534765535812"), "C");
        hashMap.put(new BigInteger("120831746580"), "c");
        hashMap.put(new BigInteger("1093111284348"), "D");
        hashMap.put(new BigInteger("120831742718"), "d");
        hashMap.put(new BigInteger("1093650252418"), "E");
        hashMap.put(new BigInteger("120966490650"), "e");
        hashMap.put(new BigInteger("1093616566400"), "F");
        hashMap.put(new BigInteger("534765544124"), "G");
        hashMap.put(new BigInteger("107997373758"), "g");
        hashMap.put(new BigInteger("1091460669694"), "H");
        hashMap.put(new BigInteger("1091192234014"), "h");
        hashMap.put(new BigInteger("17213555452"), "J");
        hashMap.put(new BigInteger("25786646974"), "j");
        hashMap.put(new BigInteger("1091460681870"), "K");
        hashMap.put(new BigInteger("1090955379202"), "L");
        hashMap.put(new BigInteger("1091997548798"), "M");
        hashMap.put(new BigInteger("266826424350"), "m");
        hashMap.put(new BigInteger("1091997536510"), "N");
        hashMap.put(new BigInteger("266826948638"), "n");
        hashMap.put(new BigInteger("534765535868"), "O");
        hashMap.put(new BigInteger("120831746588"), "o");
        hashMap.put(new BigInteger("1093616574528"), "P");
        hashMap.put(new BigInteger("270920852504"), "p");
        hashMap.put(new BigInteger("534765536378"), "Q");
        hashMap.put(new BigInteger("103685559359"), "q");
        hashMap.put(new BigInteger("1093616574558"), "R");
        hashMap.put(new BigInteger("266558513168"), "r");
        hashMap.put(new BigInteger("294786343580"), "S");
        hashMap.put(new BigInteger("78016817700"), "s");
        hashMap.put(new BigInteger("551919976576"), "T");
        hashMap.put(new BigInteger("1082365444860"), "U");
        hashMap.put(new BigInteger("257731723838"), "u");
        hashMap.put(new BigInteger("1030993612016"), "V");
        hashMap.put(new BigInteger("240585409592"), "v");
        hashMap.put(new BigInteger("1090989327614"), "W");
        hashMap.put(new BigInteger("257732510270"), "w");
        hashMap.put(new BigInteger("611229651086"), "X");
        hashMap.put(new BigInteger("146364961826"), "x");
        hashMap.put(new BigInteger("550833635456"), "Y");
        hashMap.put(new BigInteger("244897350974"), "y");
        hashMap.put(new BigInteger("577850483394"), "Z");
        hashMap.put(new BigInteger("146669187618"), "z");
    }

    private BufferedImage _img;

    /**
     * Creates a new NameProcessor object with the specified BufferedImage.
     * @param img The image.
     */
    public NameProcessor(@NotNull BufferedImage img) {
        this._img = img;
    }

    /**
     * Creates a new NameProcessor object with the specified path to the image.
     * @param imgPath The path to the image.
     * @throws IOException If the path is invalid.
     */
    public NameProcessor(@NotNull Path imgPath) throws IOException {
        this._img = ImageIO.read(imgPath.toFile());
    }

    /**
     * Creates a new NameProcessor object with the specified URL.
     * @param link The link to the image.
     * @throws IOException If the URL is invalid.
     */
    public NameProcessor(@NotNull URL link) throws IOException {
        this._img = ImageIO.read(link);
    }

    /**
     * Crops the image so there is only text. This might screw up if there are other elements with the same color as the rank colors.
     * @return The object.
     * @throws InvalidImageException If the image is not valid (i.e. if the image given has no detectable colors).
     */
    public NameProcessor cropImage() throws InvalidImageException, IOException {
        int startingXVal;
        int startingYVal;
        int minStartingXVal = this._img.getWidth();
        int minStartingYVal = this._img.getHeight();
        // left to right, top to bottom
        for (int y = 0; y < this._img.getHeight(); y++) {
            for (int x = 0; x < this._img.getWidth(); x++) {
                Color color = new Color(this._img.getRGB(x, y));
                if (this.isValidColor(color)) {
                    if (x < minStartingXVal) {
                        minStartingXVal = x;
                        System.out.println("X => " + x + " | " + y);
                    }

                    if (y < minStartingYVal) {
                        minStartingYVal = y;
                        System.out.println("Y => " + x + " | " + y);
                    }
                }
            }
        }


        startingXVal = minStartingXVal;
        startingYVal = minStartingYVal;
        System.out.println(startingXVal + " | " + startingYVal);


        if (startingXVal == this._img.getWidth() && startingYVal == this._img.getHeight()) {
            throw new InvalidImageException("invalid image");
        }

        // right to left, top to bottom
        int finalXVal;
        int mostXVal = -1;
        for (int y = 0; y < this._img.getHeight(); y++) {
            for (int x = this._img.getWidth() - 1; x >= 0; x--) {
                Color color = new Color(this._img.getRGB(x, y));
                if (this.isValidColor(color)) {
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
                Color color = new Color(this._img.getRGB(x, y));
                if (this.isValidColor(color)) {
                    if (y > mostYVal) {
                        mostYVal = y;
                    }
                }
            }
        }

        finalYVal = this._img.getHeight() - mostYVal > 0
            ? ++mostYVal
            : mostYVal;

        if (finalXVal == -1 || finalYVal == -1) {
            throw new InvalidImageException("invalid image given");
        }

        // make new copy of the image
        this._img = this.cropAndGetNewImage(this._img, startingXVal, startingYVal, finalXVal - startingXVal, finalYVal - startingYVal);
        return this;
    }

    private void savePicturesDebug(BufferedImage image) throws IOException {
        File outputfile = new File("C:\\Users\\ewang\\Desktop\\Output\\out.png");
        ImageIO.write(image, "png", outputfile);
    }

    /**
     * Gets the player names.
     * @return A list of names.
     */
    public List<String> getPlayerNames() {
        // TODO automatically determine this information
        int widthOfPixel = 0;

        List<String> nameArray = new ArrayList<>();
        // max 16 names anyways
        // add 2 extra in case of invalid input
        for (int i = 0; i < 18; i++) {
            String name = "";
            int x = 0;
            while (true) {
                String bitVal = "0";
                /*
                while (bitVal == -1 || bitVal % 256 != 0) {
                    if (bitVal < 0) {
                        bitVal = 0;
                    }

                    bitVal *= 256;
                    int t = 0;
                    for (int dy = 0; dy < 8 * widthOfPixel; dy += widthOfPixel) {

                    }
                }
                 */
            }
        }
        return new ArrayList<>();
    }

    private boolean isValidColor(Color color) {
        return color.getRGB() == MVPPlusPlus.getRGB()
                || color.getRGB() == MVPPlus.getRGB()
                || color.getRGB() == MVP.getRGB()
                || color.getRGB() == VIPPlus.getRGB()
                || color.getRGB() == VIP.getRGB()
                || color.getRGB() == NONE.getRGB();
    }

    private BufferedImage cropAndGetNewImage(BufferedImage originalImage, int x, int y, int dx, int dy) {
        BufferedImage img = originalImage.getSubimage(x, y, dx, dy);
        BufferedImage copyOfImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = copyOfImage.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        return copyOfImage;
    }

    public BufferedImage getImage() {
        return this._img;
    }
}

