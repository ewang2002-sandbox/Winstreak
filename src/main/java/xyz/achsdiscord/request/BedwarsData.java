package xyz.achsdiscord.request;

public final class BedwarsData {
    public final int kills;
    public final int deaths;
    public final double killDeathRatio;
    public final int finalKills;
    public final int finalDeaths;
    public final double finalKillDeathRatio;
    public final int wins;
    public final int losses;
    public final double winLossRatio;
    public final int bedsBroken;

    public BedwarsData(
            int kills,
            int deaths,
            int finalKills,
            int finalDeaths,
            int wins,
            int losses,
            int bedsBroken
    ) {
        this.kills = kills;
        this.deaths = deaths;
        this.killDeathRatio = deaths == 0
                ? -1
                : (double) kills / deaths;
        this.finalKills = finalKills;
        this.finalDeaths = finalDeaths;
        this.finalKillDeathRatio = finalDeaths == 0
                ? -1
                : (double) finalKills / finalDeaths;
        this.wins = wins;
        this.losses = losses;
        this.winLossRatio = losses == 0
                ? -1
                : (double) wins / losses;
        this.bedsBroken = bedsBroken;

    }
}
