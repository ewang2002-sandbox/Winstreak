package xyz.achsdiscord.parse.v2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LobbyNameParser {
    // private general variables
    protected BufferedImage _img;
    protected int _width;

    // determining starting position
    protected int _x;
    protected int _y;

    public LobbyNameParser(File file) throws IOException {
        this._img = ImageIO.read(file);
    }
}
