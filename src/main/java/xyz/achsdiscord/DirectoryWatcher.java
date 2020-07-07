package xyz.achsdiscord;

import xyz.achsdiscord.parse.AbstractNameParser;
import xyz.achsdiscord.parse.InGameNameParser;
import xyz.achsdiscord.parse.InvalidImageException;
import xyz.achsdiscord.parse.LobbyNameParser;
import xyz.achsdiscord.request.MultipleRequestHandler;
import xyz.achsdiscord.request.checker.ResponseCheckerResults;
import xyz.achsdiscord.request.checker.ResponseParser;
import xyz.achsdiscord.request.checker.TeamInfo;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DirectoryWatcher {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_GREEN = "\u001B[32m";

    public static int dirWatchFinalKills;
    public static int dirWatchBrokenBeds;
    public static int dirWatchTryhards;
    public static List<String> dirWatchExemptPlayers;

    static {
        final List<String> exemptPlayers = new ArrayList<>();
        exemptPlayers.add("CM19");
        exemptPlayers.add("icicl");
        exemptPlayers.add("Sichrylan");
        exemptPlayers.add("hedwig10");
        exemptPlayers.add("stanner321");
        dirWatchExemptPlayers = exemptPlayers;
    }

    public static void startMonitoring(
            final Path path,
            final int finalKills,
            final int brokenBeds,
            final int amtTryHardsUntilLeave
    ) {
        dirWatchFinalKills = finalKills;
        dirWatchBrokenBeds = brokenBeds;
        dirWatchTryhards = amtTryHardsUntilLeave;

        // initialize watchers
        WatchService watchService;
        WatchKey key;
        try {
            watchService = FileSystems.getDefault().newWatchService();
            path.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.OVERFLOW
            );
            key = watchService.take();
        } catch (Exception e) {
            System.out.println("[ERROR] Something went wrong when trying to initialize events. See stack trace below.");
            e.printStackTrace();
            return;
        }

        // continuously check folder
        // also intellij, stop telling
        // me to not use infinite loop
        for (; ; ) {
            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    try {
                        if (event.context().toString().endsWith(".png")) {
                            File file = Path.of(path.toString(), event.context().toString()).toFile();

                            // wait .35 second so the file
                            // is fully written
                            TimeUnit.MILLISECONDS.sleep(350);

                            // process file
                            System.out.println("[INFO] Checking: " + event.context().toString());

                            if (AbstractNameParser.isInLobby(ImageIO.read(file))) {
                                lobbyCheck(file);
                            } else {
                                inGameCheck(file);
                            }

                        }
                    } catch (Exception e) {
                        System.out.println("[ERROR] Error when checking file. See error below.");
                        e.printStackTrace();
                    }
                }
            }

            // use thread.sleep to reduce
            // cpu usage
            try {
                Thread.sleep(1);
            } catch (Exception e) {
                // doesn't matter here.
            }
        }
        // end loop
    }

    public static void lobbyCheck(final File file) throws IOException, InvalidImageException, ExecutionException, InterruptedException {
        long startImageProcessing = System.nanoTime();

        LobbyNameParser names = new LobbyNameParser(file);
        names.cropImageIfFullScreen();
        names.adjustColors();
        names.cropHeaderAndFooter();
        names.fixImage();
        names.identifyWidth();

        List<String> allNames = names.getPlayerNames(dirWatchExemptPlayers);
        long endImageProcessing = System.nanoTime();

        long startTimeForReq = System.nanoTime();
        Map<String, String> nameData = new MultipleRequestHandler(allNames)
                .sendRequests();
        ResponseParser checker = new ResponseParser(nameData)
                .setMinimumBrokenBedsNeeded(dirWatchBrokenBeds)
                .setMinimumFinalKillsNeeded(dirWatchFinalKills);

        List<ResponseCheckerResults> namesToWorryAbout = checker.getNamesToWorryAbout();
        long endTimeForReq = System.nanoTime();

        double imageProcessingTime = (endImageProcessing - startImageProcessing) * 1e-9;
        double apiRequestsTime = (endTimeForReq - startTimeForReq) * 1e-9;

        int tryhardBedsBroken = 0;
        int tryhardFinalKills = 0;
        if (namesToWorryAbout.size() != 0) {
            StringBuilder builder = new StringBuilder();
            for (ResponseCheckerResults result : namesToWorryAbout) {
                tryhardBedsBroken += result.bedsDestroyed;
                tryhardFinalKills += result.finalKills;
                builder.append("[PLAYER] Name: ").append(result.name).append(" (K = ").append(result.finalKills).append("; B = ").append(result.bedsDestroyed).append(")")
                        .append(System.lineSeparator());
            }
            System.out.println(builder.toString());
        }

        System.out.println("[INFO] Errored: " + checker.getErroredUsers().size() + " " + checker.getErroredUsers().toString());
        System.out.println("[INFO] Tryhards: " + namesToWorryAbout.size());
        System.out.println("[INFO] Total: " + allNames.size());
        System.out.println("[INFO] All Names: " + allNames.toString());
        System.out.println("[INFO] Tryhard Final Kills: " + tryhardFinalKills);
        System.out.println("[INFO] Tryhard Broken Beds: " + tryhardBedsBroken);
        System.out.println("[INFO] Total Final Kills: " + checker.getTotalFinalKills());
        System.out.println("[INFO] Total Broken Beds: " + checker.getTotalBrokenBeds());
        System.out.println("[INFO] Image Processing Time: " + imageProcessingTime + " SEC.");
        System.out.println("[INFO] API Requests Time: " + apiRequestsTime + " SEC.");

        int points = 0;
        if (namesToWorryAbout.size() >= dirWatchTryhards) {
            points += 20;
        }
        else {
            points += namesToWorryAbout.size() * 2;
        }

        points += checker.getErroredUsers().size();

        if (namesToWorryAbout.size() != 0) {
            int bedsThousands = tryhardBedsBroken / 1050;
            points += bedsThousands;

            int finalKillsThousands = tryhardFinalKills / 1350;
            points += finalKillsThousands;

            double percentBedsBrokenByTryhards = (double) tryhardBedsBroken / checker.getTotalBrokenBeds();
            int bedsBrokenMultiplier = tryhardBedsBroken >= dirWatchBrokenBeds * namesToWorryAbout.size()
                    ? 1
                    : 0;
            if (percentBedsBrokenByTryhards > 0.4) {
                points += (percentBedsBrokenByTryhards * 8) * bedsBrokenMultiplier;
            }

            double percentFinalKillsByTryhards = (double) tryhardFinalKills / checker.getTotalFinalKills();
            double finalKillsMultiplier = tryhardFinalKills >= dirWatchTryhards * namesToWorryAbout.size()
                    ? 1
                    : 0;
            if (percentFinalKillsByTryhards > 0.5) {
                points += (percentFinalKillsByTryhards * 6) * finalKillsMultiplier;
            }
        }
        else {
            int bedsThousands = checker.getTotalBrokenBeds() / 1050;
            points += bedsThousands;

            int finalKillsThousands = checker.getTotalFinalKills() / 1500;
            points += finalKillsThousands;
        }

        System.out.println("[INFO] Points: " + points);
        // 16 to inf
        if (points >= 17) {
            System.out.println(ANSI_RED + "[INFO] Suggested Action: LEAVE" + ANSI_RESET);
        }
        else if (points >= 14) {
            System.out.println(ANSI_RED + "[INFO] Suggested Action: SERIOUSLY CONSIDER LEAVING" + ANSI_RESET);
        }
        else if (points >= 10) {
            System.out.println(ANSI_YELLOW + "[INFO] Suggested Action: CONSIDER LEAVING" + ANSI_RESET);
        }
        else if (points >= 7) {
            System.out.println(ANSI_BLUE + "[INFO] Suggested Action: HARD GAME, CONSIDER STAYING" + ANSI_RESET);
        }
        else if (points >= 4) {
            System.out.println(ANSI_CYAN + "[INFO] Suggested Action: NORMAL GAME, CONSIDER STAYING" + ANSI_RESET);
        }
        else {
            System.out.println(ANSI_GREEN + "[INFO] Suggested Action: SAFE TO STAY!" + ANSI_RESET);
        }
        System.out.println("=====================================");
    }

    public static void inGameCheck(final File file) throws IOException, InvalidImageException, ExecutionException, InterruptedException {
        long startImageProcessing = System.nanoTime();

        InGameNameParser names = new InGameNameParser(file);
        names.cropImageIfFullScreen();
        names.adjustColors();
        names.cropHeaderAndFooter();
        names.fixImage();
        names.identifyWidth();

        Map<AbstractNameParser.TeamColors, List<String>> teammates = names.getPlayerNames(dirWatchExemptPlayers);
        long endImageProcessing = System.nanoTime();

        long startRequestTime = System.nanoTime();

        List<TeamInfo> teamInfo = new ArrayList<>();
        for (Map.Entry<AbstractNameParser.TeamColors, List<String>> entry : teammates.entrySet()) {
            Map<String, String> teamData = new MultipleRequestHandler(entry.getValue())
                    .sendRequests();
            ResponseParser parser = new ResponseParser(teamData);
            teamInfo.add(
                    new TeamInfo(
                            entry.getKey(),
                            parser.getPlayerDataFromMap(),
                            parser.getErroredUsers(),
                            parser.getTotalFinalKills(),
                            parser.getTotalBrokenBeds()
                    )
            );
        }

        teamInfo.sort((o1, o2) -> o2.totalBrokenBeds - o1.totalBrokenBeds);
        long stopRequestTime = System.nanoTime();

        long processRequestDataStart = System.nanoTime();
        int rank = 1;
        for (TeamInfo info : teamInfo) {
            StringBuilder builder = new StringBuilder();
            String allAvailablePlayers = info.availablePlayers
                    .stream()
                    .map(x -> x.name)
                    .collect(Collectors.toList()).toString();
            String allErroredPlayers = info.erroredPlayers.toString();
            builder.append("[").append(rank).append("] ").append(info.color).append(" (").append(info.availablePlayers.size() + info.erroredPlayers.size()).append(")")
                    .append(System.lineSeparator()).append("Total Final Kills: ").append(info.totalFinalKills)
                    .append(System.lineSeparator()).append("Total Broken Beds: ").append(info.totalBrokenBeds)
                    .append(System.lineSeparator()).append("Players Ranked: ").append(allAvailablePlayers)
                    .append(System.lineSeparator()).append("Errored Players: ").append(info.erroredPlayers.size()).append(" ").append(allErroredPlayers);
            System.out.println(builder.toString());
            System.out.println(System.lineSeparator());
            rank++;
        }
        long processRequestDataStop = System.nanoTime();

        double imageProcessingTime = (endImageProcessing - startImageProcessing) * 1e-9;
        double requestProcessingTime = (stopRequestTime - startRequestTime) * 1e-9;
        double processReqTime = (processRequestDataStop - processRequestDataStart) * 1e-9;

        System.out.println("[INFO] Image Processing Time: " + imageProcessingTime + " SEC.");
        System.out.println("[INFO] API Requests Time: " + requestProcessingTime + " SEC.");
        System.out.println("[INFO] Processing Requests Time: " + processReqTime + " SEC.");
        System.out.println("=====================================");

    }
}