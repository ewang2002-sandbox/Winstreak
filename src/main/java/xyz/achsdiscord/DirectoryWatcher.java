package xyz.achsdiscord;

import xyz.achsdiscord.parse.AbstractNameParser;
import xyz.achsdiscord.parse.InGameNameParser;
import xyz.achsdiscord.parse.InvalidImageException;
import xyz.achsdiscord.request.checker.ResponseParser;
import xyz.achsdiscord.request.checker.ResponseCheckerResults;
import xyz.achsdiscord.parse.LobbyNameParser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    public static void lobbyCheck(final File file) throws IOException, InvalidImageException {
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
        ResponseParser checker = new ResponseParser(allNames)
                .setMinimumBrokenBedsNeeded(dirWatchBrokenBeds)
                .setMinimumFinalKillsNeeded(dirWatchFinalKills);

        List<ResponseCheckerResults> results = checker.check();
        long endTimeForReq = System.nanoTime();

        double imageProcessingTime = (endImageProcessing - startImageProcessing) * 1e-9;
        double apiRequestsTime = (endTimeForReq - startTimeForReq) * 1e-9;

        int tryhardBedsBroken = 0;
        int tryhardFinalKills = 0;
        if (results.size() != 0) {
            StringBuilder builder = new StringBuilder();
            for (ResponseCheckerResults result : results) {
                tryhardBedsBroken += result.bedsDestroyed;
                tryhardFinalKills += result.finalKills;
                builder.append("[PLAYER] Name: ").append(result.name).append(" (K = ").append(result.finalKills).append("; B = ").append(result.bedsDestroyed).append(")")
                        .append(System.lineSeparator());
            }
            System.out.println(builder.toString());
        }

        System.out.println("[INFO] Errored: " + checker.getErroredUsers().size());
        System.out.println("[INFO] Tryhards: " + results.size());
        System.out.println("[INFO] Total: " + allNames.size());
        System.out.println("[INFO] All Names: " + allNames.toString());
        System.out.println("[INFO] Tryhard Final Kills: " + tryhardFinalKills);
        System.out.println("[INFO] Tryhard Broken Beds: " + tryhardBedsBroken);
        System.out.println("[INFO] Total Final Kills: " + checker.getTotalFinalKills());
        System.out.println("[INFO] Total Broken Beds: " + checker.getTotalBrokenBeds());
        System.out.println("[INFO] Image Processing Time: " + imageProcessingTime + " SEC.");
        System.out.println("[INFO] API Requests Time: " + apiRequestsTime + " SEC.");

        File outputfile = new File("C:\\Users\\ewang\\Desktop\\imageGAME.png");
        ImageIO.write(names.getImage(), "png", outputfile);
        // TODO implement point system
        System.out.println("=====================================");
    }

    public static void inGameCheck(final File file) throws IOException, InvalidImageException {
        long startImageProcessing = System.nanoTime();

        InGameNameParser names = new InGameNameParser(file);
        names.cropImageIfFullScreen();
        names.adjustColors();
        names.cropHeaderAndFooter();
        names.fixImage();
        names.identifyWidth();

        Map<AbstractNameParser.TeamColors, List<String>> teammates = names.getPlayerNames();
        long endImageProcessing = System.nanoTime();


    }
}