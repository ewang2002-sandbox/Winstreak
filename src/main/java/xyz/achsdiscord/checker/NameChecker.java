package xyz.achsdiscord.checker;

import xyz.achsdiscord.request.BedwarsData;
import xyz.achsdiscord.request.HypixelRequest;
import xyz.achsdiscord.request.RequestParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;

public class NameChecker {
    private final List<String> _names;
    private int _minimumBrokenBeds;
    private int _minimumFinalKills;

    public NameChecker(List<String> names) {
        this(names, 250, 500);
    }

    public NameChecker(List<String> names, int minBeds, int minFinalKills) {
        this._minimumFinalKills = minFinalKills;
        this._minimumBrokenBeds = minBeds;
        this._names = names;
    }

    public NameChecker setMinimumBrokenBedsNeeded(int minBeds) {
        this._minimumBrokenBeds = minBeds;
        return this;
    }

    public NameChecker setMinimumFinalKillsNeeded(int minFinalKills) {
        this._minimumFinalKills = minFinalKills;
        return this;
    }

    public List<NameCheckerResults> check() {
        @SuppressWarnings("unchecked")
        FutureTask[] nameResponses = new FutureTask[this._names.size()];
        for (int i = 0; i < this._names.size(); i++) {
            HypixelRequest req = new HypixelRequest(this._names.get(i));
            nameResponses[i] = new FutureTask<>(req);

            Thread t = new Thread(nameResponses[i]);
            t.start();
        }

        List<NameCheckerResults> namesToWorryAbout = new ArrayList<>();

        for (int i = 0; i < nameResponses.length; i++) {
            try {
                BedwarsData data = new RequestParser(this._names.get(i), (String) nameResponses[i].get())
                        .parse()
                        .getTotalDataInfo();

                if (data.bedsBroken >= this._minimumBrokenBeds || data.finalKills >= this._minimumFinalKills) {
                    NameCheckerResults results = new NameCheckerResults(this._names.get(i), data.bedsBroken, data.finalKills);
                    namesToWorryAbout.add(results);
                }
            }
            catch (Exception e) {
                // nothing for now
                // probably a request error.
            }
        }

        return namesToWorryAbout;
    }
}
