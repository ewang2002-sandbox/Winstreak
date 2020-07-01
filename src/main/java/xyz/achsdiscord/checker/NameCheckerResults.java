package xyz.achsdiscord.checker;

public class NameCheckerResults {
    public final String name;
    public final int bedsDestroyed;
    public final int finalKills;

    public NameCheckerResults(String name, int bedsDestroyed, int finalKills) {
        this.name = name;
        this.bedsDestroyed = bedsDestroyed;
        this.finalKills = finalKills;
    }
}
