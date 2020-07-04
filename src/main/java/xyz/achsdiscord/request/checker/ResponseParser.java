package xyz.achsdiscord.request.checker;

import xyz.achsdiscord.request.BedwarsData;
import xyz.achsdiscord.request.PlanckeAPIRequester;
import xyz.achsdiscord.request.RequestParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;

public class ResponseParser {
    private final List<String> _names;
    private int _minimumBrokenBeds;
    private int _minimumFinalKills;
    private int _totalBedsDestroyed;
    private int _totalFinalKills;

    /**
     * Creates a new NameChecker class with a list of given names and the default parameters of 250 broken beds and 500 final kills.
     *
     * @param names The list of names.
     */
    public ResponseParser(List<String> names) {
        this(names, 250, 500);
    }

    /**
     * Creates a new NameChecker class with a list of given names and the specified number of broken beds and final kills.
     *
     * @param names         The list of names to check
     * @param minBeds       The minimum number of beds a person must have broken in order to be reported as a tryhard.
     * @param minFinalKills The minimum number of final kills a person must have in order to be reported as a tryhard.
     */
    public ResponseParser(List<String> names, int minBeds, int minFinalKills) {
        this._minimumFinalKills = minFinalKills;
        this._minimumBrokenBeds = minBeds;
        this._names = names;
    }

    /**
     * Sets the number of beds a person must have broken in order to be reported as a tryhard.
     *
     * @param minBeds The number of broken beds.
     * @return This object.
     */
    public ResponseParser setMinimumBrokenBedsNeeded(int minBeds) {
        this._minimumBrokenBeds = minBeds;
        return this;
    }

    /**
     * Sets the number of final kills a person must have broken in order to be reported as a tryhard.
     *
     * @param minFinalKills The number of final kills.
     * @return This object.
     */
    public ResponseParser setMinimumFinalKillsNeeded(int minFinalKills) {
        this._minimumFinalKills = minFinalKills;
        return this;
    }

    /**
     * Checks all names and sees which players are considered tryhards.
     *
     * @return A list of names that meet the minimum final kills and broken beds needed to be considered a tryhard.
     */
    public List<ResponseCheckerResults> check() {
        FutureTask[] nameResponses = new FutureTask[this._names.size()];
        for (int i = 0; i < this._names.size(); i++) {
            PlanckeAPIRequester req = new PlanckeAPIRequester(this._names.get(i));
            nameResponses[i] = new FutureTask<>(req);

            Thread t = new Thread(nameResponses[i]);
            t.start();
        }

        List<ResponseCheckerResults> namesToWorryAbout = new ArrayList<>();

        int totalBrokenBeds = 0;
        int totalFinalKills = 0;

        for (int i = 0; i < nameResponses.length; i++) {
            try {
                BedwarsData data = new RequestParser(this._names.get(i), (String) nameResponses[i].get())
                        .parse()
                        .getTotalDataInfo();
                totalBrokenBeds += data.bedsBroken;
                totalFinalKills += data.finalKills;
                if (data.bedsBroken >= this._minimumBrokenBeds || data.finalKills >= this._minimumFinalKills) {
                    ResponseCheckerResults results = new ResponseCheckerResults(this._names.get(i), data.bedsBroken, data.finalKills);
                    namesToWorryAbout.add(results);
                }
            } catch (Exception e) {
                System.out.println("[ERROR] An error occurred when checking " + this._names.get(i) + "'s profile.");
            }
        }

        this._totalFinalKills = totalFinalKills;
        this._totalBedsDestroyed = totalBrokenBeds;
        return namesToWorryAbout;
    }

    /**
     * Gets the number of final kills across all players in the list (passed in the constructor).
     * You must have called the {@code check()} method first.
     *
     * @return The number of final kills across all players.
     */
    public int getTotalFinalKills() {
        return this._totalFinalKills;
    }

    /**
     * Gets the number of broken beds across all players in the list (passed in the constructor)
     * You must have called the {@code check()} method first.
     *
     * @return The number of broken beds across all players.
     */
    public int getTotalBrokenBeds() {
        return this._totalBedsDestroyed;
    }
}
