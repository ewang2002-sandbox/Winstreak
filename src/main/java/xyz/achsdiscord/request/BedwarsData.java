package xyz.achsdiscord.request;

public final class BedwarsData {
    public final int kills;
    public final int deaths;
    public final int finalKills;
    public final int finalDeaths;
    public final int wins;
    public final int losses;
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
        this.finalKills = finalKills;
        this.finalDeaths = finalDeaths;
        this.wins = wins;
        this.losses = losses;
        this.bedsBroken = bedsBroken;
    }
}
