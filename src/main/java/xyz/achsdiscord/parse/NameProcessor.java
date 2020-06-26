package xyz.achsdiscord.parse;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import xyz.achsdiscord.classes.RankColor;
import xyz.achsdiscord.util.Utility;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

public class NameProcessor {
    public static final RankColor[] rankColors = {
            new RankColor("MVP++", 255, 170, 0, 30),
            new RankColor("MVP+", 85, 255, 255, 30),
            new RankColor("MVP", 85, 255, 255, 30),
            new RankColor("VIP+", 85, 255, 85, 30),
            new RankColor("VIP", 85, 255, 85, 30),
            new RankColor("NONE", 170, 170, 170, 10)
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
                    if (colorsAreClose(color, rankColor.color, rankColor.threshold)) {
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

    public String processImage() {
        String ocrResults = null;
        try {
            ITesseract tesseract = new Tesseract();
            tesseract.setDatapath(Utility.getPathOfResource("/tessdata"));
            tesseract.setLanguage("eng");
            ocrResults = tesseract.doOCR(this._img);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ocrResults;
    }

    public BufferedImage getImage() {
        return this._img;
    }

    private boolean colorsAreClose(Color a, Color z, int threshold) {
        int r = a.getRed() - z.getRed();
        int g = a.getGreen() - z.getGreen();
        int b = a.getBlue() - z.getBlue();
        return (r * r + g * g + b * b) <= threshold * threshold;
    }

    private boolean colorsAreClose(Color a, Color z) {
        return this.colorsAreClose(a, z, 50);
    }
}
