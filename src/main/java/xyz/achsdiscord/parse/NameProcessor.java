
package xyz.achsdiscord.parse;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NameProcessor {
    public static final Color MVPPlusPlus = new Color(255, 170, 0);
    public static final Color MVPPlus = new Color(85, 255, 255);
    public static final Color MVP = new Color(85, 255, 255);
    public static final Color VIPPlus = new Color(85, 255, 85);
    public static final Color VIP = new Color(85, 255, 85);
    public static final Color NONE = new Color(170, 170, 170);

    public static final Map<String, String> hashMap;

    static {
        hashMap = new HashMap<>();
        hashMap.put("011111001111111010000010101000101011111010111100","");
        hashMap.put("111111101111111010100000101000001111111001011110","");
        hashMap.put("100000001100000001111110011111101100000010000000","");
        hashMap.put("111111101111111010100010101000101111111001011100","");
        hashMap.put("0111110010001010100100101010001001111100","0");
        hashMap.put("0000001001000010111111100000001000000010","1");
        hashMap.put("0100011010001010100100101001001001100110","2");
        hashMap.put("0100010010000010100100101001001001101100","3");
        hashMap.put("0001100000101000010010001000100011111110","4");
        hashMap.put("1110010010100010101000101010001010011100","5");
        hashMap.put("0011110001010010100100101001001000001100","6");
        hashMap.put("1100000010000000100011101001000011100000","7");
        hashMap.put("0110110010010010100100101001001001101100","8");
        hashMap.put("0110000010010010100100101001010001111000","9");
        hashMap.put("0000000100000001000000010000000100000001","_");
        hashMap.put("0111111010100000101000001010000001111110","A");
        hashMap.put("0000010000101010001010100010101000011110","a");
        hashMap.put("1111111010100010101000101010001001011100","B");
        hashMap.put("1111111000010010001000100010001000011100","b");
        hashMap.put("0111110010000010100000101000001001000100","C");
        hashMap.put("0001110000100010001000100010001000010100","c");
        hashMap.put("1111111010000010100000101000001001111100","D");
        hashMap.put("0001110000100010001000100001001011111110","d");
        hashMap.put("1111111010100010101000101000001010000010","E");
        hashMap.put("0001110000101010001010100010101000011010","e");
        hashMap.put("1111111010100000101000001000000010000000","F");
        hashMap.put("00100000011111101010000010100000","f");
        hashMap.put("0111110010000010100000101010001010111100","G");
        hashMap.put("0001100100100101001001010010010100111110","g");
        hashMap.put("1111111000100000001000000010000011111110","H");
        hashMap.put("1111111000010000001000000010000000011110","h");
        hashMap.put("100000101111111010000010","I");
        hashMap.put("10111110","i");
        hashMap.put("0000010000000010000000100000001011111100","J");
        hashMap.put("0000011000000001000000010000000110111110","j");
        hashMap.put("1111111000100000001000000101000010001110","K");
        hashMap.put("11111110000010000001010000100010","k");
        hashMap.put("1111111000000010000000100000001000000010","L");
        hashMap.put("1111110000000010","l");
        hashMap.put("1111111001000000001000000100000011111110","M");
        hashMap.put("0011111000100000000110000010000000011110","m");
        hashMap.put("1111111001000000001000000001000011111110","N");
        hashMap.put("0011111000100000001000000010000000011110","n");
        hashMap.put("0111110010000010100000101000001001111100","O");
        hashMap.put("0001110000100010001000100010001000011100","o");
        hashMap.put("1111111010100000101000001010000001000000","P");
        hashMap.put("0011111100010100001001000010010000011000","p");
        hashMap.put("0111110010000010100000101000010001111010","Q");
        hashMap.put("0001100000100100001001000001010000111111","q");
        hashMap.put("1111111010100000101000001010000001011110","R");
        hashMap.put("0011111000010000001000000010000000010000","r");
        hashMap.put("0100010010100010101000101010001010011100","S");
        hashMap.put("0001001000101010001010100010101000100100","s");
        hashMap.put("1000000010000000111111101000000010000000","T");
        hashMap.put("001000001111110000100010","t");
        hashMap.put("1111110000000010000000100000001011111100","U");
        hashMap.put("0011110000000010000000100000001000111110","u");
        hashMap.put("1111000000001100000000100000110011110000","V");
        hashMap.put("0011100000000100000000100000010000111000","v");
        hashMap.put("1111111000000100000010000000010011111110","W");
        hashMap.put("0011110000000010000011100000001000111110","w");
        hashMap.put("1000111001010000001000000101000010001110","X");
        hashMap.put("0010001000010100000010000001010000100010","x");
        hashMap.put("1000000001000000001111100100000010000000","Y");
        hashMap.put("0011100100000101000001010000010100111110","y");
        hashMap.put("1000011010001010100100101010001011000010","Z");
        hashMap.put("0010001000100110001010100011001000100010","z");
    }

    private BufferedImage _img;

    /**
     * Creates a new NameProcessor object with the specified BufferedImage.
     *
     * @param img The image.
     */
    public NameProcessor(BufferedImage img) {
        this._img = img;
    }

    /**
     * Creates a new NameProcessor object with the specified path to the image.
     *
     * @param imgPath The path to the image.
     * @throws IOException If the path is invalid.
     */
    public NameProcessor(Path imgPath) throws IOException {
        this._img = ImageIO.read(imgPath.toFile());
    }

    /**
     * Creates a new NameProcessor object with the specified URL.
     *
     * @param link The link to the image.
     * @throws IOException If the URL is invalid.
     */
    public NameProcessor(URL link) throws IOException {
        this._img = ImageIO.read(link);
    }

    /**
     * Fixes the image, essentially replacing every pixel with either white or black. This should be called first.
     *
     * @return The object.
     */
    public NameProcessor fixImage() {
        for (int y = 0; y < this._img.getHeight(); y++) {
            for (int x = 0; x < this._img.getWidth(); x++) {
                Color color = new Color(this._img.getRGB(x, y));

                boolean isChanged = false;
                if (this.isValidColor(color)) {
                    isChanged = true;
                    this._img.setRGB(x, y, Color.black.getRGB());
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
     *
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

        // make new copy of the image
        this._img = this.cropAndGetNewImage(this._img, startingXVal, startingYVal, this._img.getWidth() - startingXVal, this._img.getHeight() - startingYVal);
        // now we need to determine where to start

        return this;
    }

    /**
     * Gets the player names.
     *
     * @return A list of names.
     */
    public List<String> getPlayerNames() {
        // will contain list of names
        List<String> names = new ArrayList<>();
        int width = 2;
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
                        for (int dy = 0; dy < 8 * width; dy += width) {
                            if (this._img.getRGB(x, y + dy) == Color.black.getRGB()) {
                                columnBytes.append("1");
                            }
                            else {
                                columnBytes.append("0");
                            }
                        }

                        ttlBytes.append(columnBytes.toString());
                        x += width;
                    }
                    catch (Exception e) {
                        // this is probably due to poor cropping
                        // or any extra useless characters.
                        errored = true;
                        break;
                    }
                }

                if (!errored) {
                    ttlBytes = new StringBuilder(ttlBytes.substring(0, ttlBytes.length() - 8));
                }

                if (hashMap.containsKey(ttlBytes.toString())) {
                    name.append(hashMap.get(ttlBytes.toString()));
                }
                else {
                    break;
                }
            }
            names.add(name.toString());
            // 8 + 1 means the names + the space
            // that separates the first name from
            // the next one
            y += 9 * width;
        }

        names = names.stream()
                .filter(name -> name.length() != 0)
                .collect(Collectors.toList());
        return names;
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
