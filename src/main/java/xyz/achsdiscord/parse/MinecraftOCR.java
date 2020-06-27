package xyz.achsdiscord.parse;

import xyz.achsdiscord.util.Utility;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MinecraftOCR {
    public static final HashMap<String, BufferedImage> characterImageMap;
    private static boolean hasBeenProcessed = false;

    private List<BufferedImage> _images;

    public MinecraftOCR(List<BufferedImage> images) {
        this._images = images;
    }

    public MinecraftOCR ensureProcessed() {
        if (!MinecraftOCR.hasBeenProcessed) {
            for (BufferedImage image : MinecraftOCR.characterImageMap.values()) {

            }

            MinecraftOCR.hasBeenProcessed = true;
        }
        return this;
    }

    public List<String> parse() {
        List<String> output = new ArrayList<>();
        for (BufferedImage character : this._images) {

        }

        return output;
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
