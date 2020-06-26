package xyz.achsdiscord.classes;

import java.awt.*;

public class RankColor {
    public String rank;
    public Color color;
    public int threshold;

    public RankColor(String rank, int r, int g, int b, int threshold) {
        this.rank = rank;
        this.color = new Color(r, g, b);
        this.threshold = threshold;
    }
}
