package xyz.achsdiscord;

import xyz.achsdiscord.checker.NameChecker;
import xyz.achsdiscord.checker.NameCheckerResults;
import xyz.achsdiscord.parse.NameProcessor;

import java.io.File;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DirectoryWatcher {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_GREEN = "\u001B[32m";


    public static void startMonitoring(
            final Path path,
            final int finalKills,
            final int brokenBeds,
            final int amtTryHardsUntilLeave
    ) {
        // people to exclude
        final List<String> exemptPlayers = new ArrayList<>();
        exemptPlayers.add("CM19");
        exemptPlayers.add("icicl");
        exemptPlayers.add("Sichrylan");
        exemptPlayers.add("hedwig10");
        exemptPlayers.add("stanner321");

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

                            long startImageProcessing = System.nanoTime();

                            List<String> names = new NameProcessor(file)
                                    .cropImageIfFullScreen()
                                    .makeBlackAndWhiteAndGetWidth()
                                    .cropHeaderAndFooter()
                                    .fixImage()
                                    .getPlayerNames(exemptPlayers);
                            long endImageProcessing = System.nanoTime();

                            long startTimeForReq = System.nanoTime();
                            NameChecker checker = new NameChecker(names)
                                    .setMinimumBrokenBedsNeeded(brokenBeds)
                                    .setMinimumFinalKillsNeeded(finalKills);

                            List<NameCheckerResults> results = checker.check();
                            long endTimeForReq = System.nanoTime();

                            double imageProcessingTime = (endImageProcessing - startImageProcessing) * 1e-9;
                            double apiRequestsTime = (endTimeForReq - startTimeForReq) * 1e-9;

                            int tryhardBedsBroken = 0;
                            int tryhardFinalKills = 0;
                            if (results.size() != 0) {
                                StringBuilder b = new StringBuilder();
                                for (NameCheckerResults result : results) {
                                    tryhardBedsBroken += result.bedsDestroyed;
                                    tryhardFinalKills += result.finalKills;
                                    b.append("[PLAYER] Name: ").append(result.name).append(" (K = ").append(result.finalKills).append("; B = ").append(result.bedsDestroyed).append(")")
                                            .append(System.lineSeparator());
                                }
                                System.out.println(b.toString());
                            }

                            System.out.println("[INFO] Tryhards: " + results.size());
                            System.out.println("[INFO] Tryhard Final Kills: " + tryhardFinalKills);
                            System.out.println("[INFO] Tryhard Broken Beds: " + tryhardBedsBroken);
                            System.out.println("[INFO] Total Final Kills: " + checker.getTotalFinalKills());
                            System.out.println("[INFO] Total Broken Beds: " + checker.getTotalBrokenBeds());
                            System.out.println("[INFO] Image Processing Time: " + imageProcessingTime + " SEC.");
                            System.out.println("[INFO] API Requests Time: " + apiRequestsTime + " SEC.");

                            boolean hasTooManyTryHards = results.size() >= amtTryHardsUntilLeave;
                            int points = 0;
                            // tryhards
                            if (hasTooManyTryHards) {
                                points += 15;
                            }
                            else {
                                points += results.size() * 2;
                            }

                            if (results.size() != 0) {
                                double percentTryHardBedsBroken = (double) tryhardBedsBroken / checker.getTotalBrokenBeds();
                                if (percentTryHardBedsBroken >= 0.6) {
                                    points += 6;
                                }
                                else if (percentTryHardBedsBroken >= 0.5) {
                                    points += 5;
                                }
                                else if (percentTryHardBedsBroken >= 0.3) {
                                    points += 1;
                                }

                                double percentTryHardFinalKills = (double) tryhardFinalKills / checker.getTotalFinalKills();
                                if (percentTryHardFinalKills >= 0.8) {
                                    points += 5;
                                }
                                else if (percentTryHardFinalKills >= 0.5) {
                                    points += 3;
                                }
                                else if (percentTryHardFinalKills >= 0.3) {
                                    points += 1;
                                }
                            }

                            // average beds between
                            if (15 <= points) {
                                System.out.println(ANSI_RED + "[INFO] Safety Level: LEAVE NOW." + ANSI_RESET);
                            }
                            else if (10 <= points) {
                                System.out.println(ANSI_YELLOW + "[INFO] Safety Level: SOMEWHAT DANGEROUS." + ANSI_RESET);
                            }
                            else if (6 <= points){
                                System.out.println(ANSI_BLUE + "[INFO] Safety Level: SOMEWHAT SAFE." + ANSI_RESET);
                            }
                            else if (3 <= points) {
                                System.out.println(ANSI_CYAN + "[INFO] Safety Label: PRETTY SAFE" + ANSI_RESET);
                            }
                            else {
                                System.out.println(ANSI_GREEN + "[INFO] Safety Level: SAFE." + ANSI_RESET);
                            }

                            System.out.println("=====================================");
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
}