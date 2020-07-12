package xyz.achsdiscord.parse;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Constants {
    // rank colors
    public static final Color MVP_PLUS_PLUS = new Color(255, 170, 0);
    public static final Color MVP_PLUS = new Color(85, 255, 255);
    public static final Color MVP = new Color(85, 255, 255);
    public static final Color VIP_PLUS = new Color(85, 255, 85);
    public static final Color VIP = new Color(85, 255, 85);
    public static final Color NONE = new Color(170, 170, 170);

    // team colors

    /**
     * All possible team colors for 3v3v3v3 and 4v4v4v4.
     */
    public enum TeamColors {
        BLUE,
        GREEN,
        RED,
        YELLOW,
        UNKNOWN
    }

    // TODO get other doubles team colors

    public static final Color BLUE_TEAM_COLOR = new Color(0x5555FF);
    public static final Color GREEN_TEAM_COLOR = new Color(0x55FF55);
    // red team color is the same as the color of
    // STORE.HYPIXEL.NET link at bottom of player list
    public static final Color RED_TEAM_COLOR = new Color(0xFF5555);
    public static final Color YELLOW_TEAM_COLOR = new Color(0xFFFF55);

    // other colors

    // this is the color that is used in
    // "You are playing on..."
    public static final Color YOU_ARE_PLAYING_ON_COLOR = new Color(85, 255, 255);
    public static final Color STORE_HYPIXEL_NET_DARK_COLOR = new Color(0x3F1515);
    public static final Color BOSS_BAR_COLOR = new Color(0x76005C);

    public static final Map<String, String> binaryToCharactersMap;

    static {
        binaryToCharactersMap = new HashMap<>();
        binaryToCharactersMap.put("011111001111111010000010101000101011111010111100", "");
        binaryToCharactersMap.put("111111101111111010100000101000001111111001011110", "");
        binaryToCharactersMap.put("100000001100000001111110011111101100000010000000", "");
        binaryToCharactersMap.put("111111101111111010100010101000101111111001011100", "");
        binaryToCharactersMap.put("0111110010001010100100101010001001111100", "0");
        binaryToCharactersMap.put("0000001001000010111111100000001000000010", "1");
        binaryToCharactersMap.put("0100011010001010100100101001001001100110", "2");
        binaryToCharactersMap.put("0100010010000010100100101001001001101100", "3");
        binaryToCharactersMap.put("0001100000101000010010001000100011111110", "4");
        binaryToCharactersMap.put("1110010010100010101000101010001010011100", "5");
        binaryToCharactersMap.put("0011110001010010100100101001001000001100", "6");
        binaryToCharactersMap.put("1100000010000000100011101001000011100000", "7");
        binaryToCharactersMap.put("0110110010010010100100101001001001101100", "8");
        binaryToCharactersMap.put("0110000010010010100100101001010001111000", "9");
        binaryToCharactersMap.put("0000000100000001000000010000000100000001", "_");
        binaryToCharactersMap.put("0111111010100000101000001010000001111110", "A");
        binaryToCharactersMap.put("0000010000101010001010100010101000011110", "a");
        binaryToCharactersMap.put("1111111010100010101000101010001001011100", "B");
        binaryToCharactersMap.put("1111111000010010001000100010001000011100", "b");
        binaryToCharactersMap.put("0111110010000010100000101000001001000100", "C");
        binaryToCharactersMap.put("0001110000100010001000100010001000010100", "c");
        binaryToCharactersMap.put("1111111010000010100000101000001001111100", "D");
        binaryToCharactersMap.put("0001110000100010001000100001001011111110", "d");
        binaryToCharactersMap.put("1111111010100010101000101000001010000010", "E");
        binaryToCharactersMap.put("0001110000101010001010100010101000011010", "e");
        binaryToCharactersMap.put("1111111010100000101000001000000010000000", "F");
        binaryToCharactersMap.put("00100000011111101010000010100000", "f");
        binaryToCharactersMap.put("0111110010000010100000101010001010111100", "G");
        binaryToCharactersMap.put("0001100100100101001001010010010100111110", "g");
        binaryToCharactersMap.put("1111111000100000001000000010000011111110", "H");
        binaryToCharactersMap.put("1111111000010000001000000010000000011110", "h");
        binaryToCharactersMap.put("100000101111111010000010", "I");
        binaryToCharactersMap.put("10111110", "i");
        binaryToCharactersMap.put("0000010000000010000000100000001011111100", "J");
        binaryToCharactersMap.put("0000011000000001000000010000000110111110", "j");
        binaryToCharactersMap.put("1111111000100000001000000101000010001110", "K");
        binaryToCharactersMap.put("11111110000010000001010000100010", "k");
        binaryToCharactersMap.put("1111111000000010000000100000001000000010", "L");
        binaryToCharactersMap.put("1111110000000010", "l");
        binaryToCharactersMap.put("1111111001000000001000000100000011111110", "M");
        binaryToCharactersMap.put("0011111000100000000110000010000000011110", "m");
        binaryToCharactersMap.put("1111111001000000001000000001000011111110", "N");
        binaryToCharactersMap.put("0011111000100000001000000010000000011110", "n");
        binaryToCharactersMap.put("0111110010000010100000101000001001111100", "O");
        binaryToCharactersMap.put("0001110000100010001000100010001000011100", "o");
        binaryToCharactersMap.put("1111111010100000101000001010000001000000", "P");
        binaryToCharactersMap.put("0011111100010100001001000010010000011000", "p");
        binaryToCharactersMap.put("0111110010000010100000101000010001111010", "Q");
        binaryToCharactersMap.put("0001100000100100001001000001010000111111", "q");
        binaryToCharactersMap.put("1111111010100000101000001010000001011110", "R");
        binaryToCharactersMap.put("0011111000010000001000000010000000010000", "r");
        binaryToCharactersMap.put("0100010010100010101000101010001010011100", "S");
        binaryToCharactersMap.put("0001001000101010001010100010101000100100", "s");
        binaryToCharactersMap.put("1000000010000000111111101000000010000000", "T");
        binaryToCharactersMap.put("001000001111110000100010", "t");
        binaryToCharactersMap.put("1111110000000010000000100000001011111100", "U");
        binaryToCharactersMap.put("0011110000000010000000100000001000111110", "u");
        binaryToCharactersMap.put("1111000000001100000000100000110011110000", "V");
        binaryToCharactersMap.put("0011100000000100000000100000010000111000", "v");
        binaryToCharactersMap.put("1111111000000100000010000000010011111110", "W");
        binaryToCharactersMap.put("0011110000000010000011100000001000111110", "w");
        binaryToCharactersMap.put("1000111001010000001000000101000010001110", "X");
        binaryToCharactersMap.put("0010001000010100000010000001010000100010", "x");
        binaryToCharactersMap.put("1000000001000000001111100100000010000000", "Y");
        binaryToCharactersMap.put("0011100100000101000001010000010100111110", "y");
        binaryToCharactersMap.put("1000011010001010100100101010001011000010", "Z");
        binaryToCharactersMap.put("0010001000100110001010100011001000100010", "z");
    }


    public static final int LISTED_NUMS_OFFSET = 30;
}
