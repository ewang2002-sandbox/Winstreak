package xyz.achsdiscord.rgbcolor;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class BetterImage {
    // width of image
    private int _width;
    // height of image
    private int _height;
    // whether there is alpha or not
    private boolean _hasAlphaChannel;
    // 3 if rgb, 4 if argb
    private int _pixelLength;
    // pixels
    private byte[] _pixels;

    public BetterImage(BufferedImage image) {
        this.construct(image);
    }

    private void construct(BufferedImage image) {
        this._pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        this._width = image.getWidth();
        this._height = image.getHeight();
        this._hasAlphaChannel = image.getAlphaRaster() != null;
        this._pixelLength = this._hasAlphaChannel
                ? 4
                : 3;
    }

    public int getRGB(int x, int y) {
        int position = (y * this._pixelLength * this._width)
                + (x * this._pixelLength);

        int argb = -16777216; // 255 alpha
        if (this._hasAlphaChannel) {
            argb = ((int) this._pixels[position++] & 0xff) << 24; // alpha
        }

        argb += (int) this._pixels[position++] & 0xff; // blue
        argb += ((int) this._pixels[position++] & 0xff) << 8; // green
        argb += ((int) this._pixels[position] & 0xff) << 16; // red
        return argb;
    }

    public int getWidth() {
        return this._width;
    }

    public int getHeight() {
        return this._height;
    }
}
