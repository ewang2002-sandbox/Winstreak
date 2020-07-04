package xyz.achsdiscord.request;

import java.util.Arrays;

public class RequestParser {
    private final String _htmlContent;
    private final String _name;
    private BedwarsData _soloDataInfo;
    private BedwarsData _doublesDataInfo;
    private BedwarsData _threesDataInfo;
    private BedwarsData _foursDataInfo;
    private BedwarsData _totalInfo;

    /**
     * Parses the raw HTML data, which was from the HypixelRequest class.
     * @param name The name associated with the raw response.
     * @param htmlContent The raw response from the request (from {@code PlanckeAPIRequester}).
     */
    public RequestParser(String name, String htmlContent) {
        this._htmlContent = htmlContent;
        this._name = name;
    }

    /**
     * Parses the raw HTML data. After this method is executed, all getters will be available for use.
     * @return This object.
     */
    public RequestParser parse() {
        // only get bedwars data
        String bedwarsData = this._htmlContent
                .split("Bed Wars </a>")[1]
                .split("Build Battle </a>")[0]
                .split("</thead>")[1]
                .split("</div>")[0];
        // clean up data
        bedwarsData = bedwarsData
                .replaceAll("<td style=\"border-right: 1px solid #f3f3f3\">", "")
                .replaceAll("<th scope=\"row\" style=\"border-right: 1px solid #f3f3f3\">", "");

        // get data for solos
        String soloData = bedwarsData
                .split("Solo")[1]
                .split("Doubles")[0]
                .replaceAll("</th><td>", "")
                .replaceAll("</td></tr><tr>", "");
        String[] soloDataArr = soloData
                .replaceAll(",", "")
                .split("</td>|<td>");
        soloDataArr = Arrays
                .stream(soloDataArr)
                .filter(x -> !x.equals(""))
                .toArray(String[]::new);
        try {
            this._soloDataInfo = new BedwarsData(
                    Integer.parseInt(soloDataArr[0]),
                    Integer.parseInt(soloDataArr[1]),
                    Integer.parseInt(soloDataArr[3]),
                    Integer.parseInt(soloDataArr[4]),
                    Integer.parseInt(soloDataArr[6]),
                    Integer.parseInt(soloDataArr[7]),
                    Integer.parseInt(soloDataArr[9])
            );
        } catch (Exception e) {
            this._soloDataInfo = null;
        }

        // get data for doubles
        String doubleData = bedwarsData
                .split("Doubles")[1]
                .split("3v3v3v3")[0]
                .replaceAll("</th><td>", "")
                .replaceAll("</td></tr><tr>", "");
        String[] doubleDataArr = doubleData
                .replaceAll(",", "")
                .split("</td>|<td>");
        doubleDataArr = Arrays
                .stream(doubleDataArr)
                .filter(x -> !x.equals(""))
                .toArray(String[]::new);

        try {
            this._doublesDataInfo = new BedwarsData(
                    Integer.parseInt(doubleDataArr[0]),
                    Integer.parseInt(doubleDataArr[1]),
                    Integer.parseInt(doubleDataArr[3]),
                    Integer.parseInt(doubleDataArr[4]),
                    Integer.parseInt(doubleDataArr[6]),
                    Integer.parseInt(doubleDataArr[7]),
                    Integer.parseInt(doubleDataArr[9])
            );
        } catch (Exception e) {
            this._doublesDataInfo = null;
        }

        // get data for 3v3v3v3
        String threeData = bedwarsData
                .split("3v3v3v3")[1]
                .split("4v4v4v4")[0]
                .replaceAll("</th><td>", "")
                .replaceAll("</td></tr><tr>", "");
        String[] threeDataArr = threeData
                .replaceAll(",", "")
                .split("</td>|<td>");
        threeDataArr = Arrays
                .stream(threeDataArr)
                .filter(x -> !x.equals(""))
                .toArray(String[]::new);
        try {
            this._threesDataInfo = new BedwarsData(
                    Integer.parseInt(threeDataArr[0]),
                    Integer.parseInt(threeDataArr[1]),
                    Integer.parseInt(threeDataArr[3]),
                    Integer.parseInt(threeDataArr[4]),
                    Integer.parseInt(threeDataArr[6]),
                    Integer.parseInt(threeDataArr[7]),
                    Integer.parseInt(threeDataArr[9])
            );
        } catch (Exception e) {
            this._threesDataInfo = null;
        }

        // get data for 4v4v4v4
        String fourData = bedwarsData
                .split("4v4v4v4")[1]
                .split("4v4")[0]
                .replaceAll("</th><td>", "")
                .replaceAll("</td></tr><tr>", "");
        String[] fourDataArr = fourData
                .replaceAll(",", "")
                .split("</td>|<td>");
        fourDataArr = Arrays
                .stream(fourDataArr)
                .filter(x -> !x.equals(""))
                .toArray(String[]::new);
        try {
            this._foursDataInfo = new BedwarsData(
                    Integer.parseInt(fourDataArr[0]),
                    Integer.parseInt(fourDataArr[1]),
                    Integer.parseInt(fourDataArr[3]),
                    Integer.parseInt(fourDataArr[4]),
                    Integer.parseInt(fourDataArr[6]),
                    Integer.parseInt(fourDataArr[7]),
                    Integer.parseInt(fourDataArr[9])
            );
        } catch (Exception e) {
            this._foursDataInfo = null;
        }

        if (this._soloDataInfo == null
                || this._doublesDataInfo == null
                || this._threesDataInfo == null
                || this._foursDataInfo == null) {
            this._totalInfo = null;
        }
        else {
            this._totalInfo = new BedwarsData(
                    this._soloDataInfo.kills + this._doublesDataInfo.kills + this._threesDataInfo.kills + this._foursDataInfo.kills,
                    this._soloDataInfo.deaths + this._doublesDataInfo.deaths + this._threesDataInfo.deaths + this._foursDataInfo.deaths,
                    this._soloDataInfo.finalKills + this._doublesDataInfo.finalKills + this._threesDataInfo.finalKills + this._foursDataInfo.finalKills,
                    this._soloDataInfo.finalDeaths + this._doublesDataInfo.finalDeaths + this._threesDataInfo.finalDeaths + this._foursDataInfo.finalDeaths,
                    this._soloDataInfo.wins + this._doublesDataInfo.wins + this._threesDataInfo.wins + this._foursDataInfo.wins,
                    this._soloDataInfo.losses + this._doublesDataInfo.losses + this._threesDataInfo.losses + this._foursDataInfo.losses,
                    this._soloDataInfo.bedsBroken + this._doublesDataInfo.bedsBroken + this._threesDataInfo.bedsBroken + this._foursDataInfo.bedsBroken
            );
        }
        return this;
    }

    public String getName() {
        return this._name;
    }

    public BedwarsData getSoloDataInfo() {
        return this._soloDataInfo;
    }

    public BedwarsData getDoublesDataInfo() {
        return this._doublesDataInfo;
    }

    public BedwarsData get3v3v3v3DataInfo() {
        return this._threesDataInfo;
    }

    public BedwarsData get4v4v4v4DataInfo() {
        return this._foursDataInfo;
    }

    public BedwarsData getTotalDataInfo() {
        return this._totalInfo;
    }
}
