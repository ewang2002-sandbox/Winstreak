package xyz.achsdiscord.classes;

import java.awt.*;

public class RankColor {
    public String rank;
    public Color color;

    public RankColor(String rank, int r, int g, int b) {
        this.rank = rank;
        this.color = new Color(r, g, b);
    }
}
