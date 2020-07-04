package xyz.achsdiscord.request.checker;

public class ResponseCheckerResults {
    public final String name;
    public final int bedsDestroyed;
    public final int finalKills;

    public ResponseCheckerResults(String name, int bedsDestroyed, int finalKills) {
        this.name = name;
        this.bedsDestroyed = bedsDestroyed;
        this.finalKills = finalKills;
    }
}
