package xyz.achsdiscord.request.checker;

import xyz.achsdiscord.parse.AbstractNameParser;

import java.util.List;

public final class TeamInfo {
    public final String color;
    public final List<ResponseCheckerResults> availablePlayers;
    public final List<String> erroredPlayers;
    public final int totalFinalKills;
    public final int totalBrokenBeds;

    public TeamInfo(
            AbstractNameParser.TeamColors color,
            List<ResponseCheckerResults> availablePlayers,
            List<String> errored,
            int totalFinals,
            int totalBroken
    ) {
        this.color = color.toString();
        this.availablePlayers = availablePlayers;
        this.erroredPlayers = errored;
        this.totalBrokenBeds = totalBroken;
        this.totalFinalKills = totalFinals;
    }
}
