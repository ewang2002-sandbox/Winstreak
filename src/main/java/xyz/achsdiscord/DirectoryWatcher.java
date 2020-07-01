package xyz.achsdiscord;

import xyz.achsdiscord.checker.NameChecker;
import xyz.achsdiscord.checker.NameCheckerResults;
import xyz.achsdiscord.parse.InvalidImageException;
import xyz.achsdiscord.parse.NameProcessor;
import xyz.achsdiscord.request.BedwarsData;
import xyz.achsdiscord.request.HypixelRequest;
import xyz.achsdiscord.request.RequestParser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class DirectoryWatcher {
    public static void startMonitoring(final Path path) {
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
        }
        catch (Exception e) {
            System.out.println("[ERROR] Something went wrong when trying to initialize events. See stack trace below.");
            e.printStackTrace();
            return;
        }

        List<String> names;
        List<NameCheckerResults> results;

        // continuously check folder
        for (;;) {
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

                            names = new NameProcessor(file)
                                    .cropImageIfFullScreen()
                                    .makeBlackAndWhiteAndGetWidth()
                                    .cropHeaderAndFooter()
                                    .fixImage()
                                    .getPlayerNames();
                            long endImageProcessing = System.nanoTime();

                            System.out.println("[INFO] Processed " + names.size() + " names in " + ((endImageProcessing - startImageProcessing) * 1e-9) + " seconds.");
                            String listOfNames = String.join(", ", names);
                            System.out.println("[INFO] Names: " + listOfNames);

                            long startTimeForReq = System.nanoTime();
                            results = new NameChecker(names)
                                    .setMinimumBrokenBedsNeeded(300)
                                    .setMinimumFinalKillsNeeded(1000)
                                    .check();
                            long endTimeForReq = System.nanoTime();
                            System.out.println("[INFO] Sent " + names.size() + " API calls in " + ((endTimeForReq - startTimeForReq) * 1e-9) + " seconds.");
                            if (results.size() == 0) {
                                System.out.println("[INFO] This lobby is good to go.");
                            }
                            else {
                                StringBuilder b = new StringBuilder();
                                for (NameCheckerResults result : results) {
                                    b.append("⇒ Name: " + result.name)
                                            .append(System.lineSeparator()).append("\tFinal Kills: ").append(result.finalKills)
                                            .append(System.lineSeparator()).append("\tBroken Beds: ").append(result.bedsDestroyed)
                                            .append(System.lineSeparator());
                                }
                                System.out.println(b.toString());
                            }

                            System.out.println("============================");
                        }
                    }
                    catch (Exception e) {
                        System.out.println("[ERROR] Error when checking file. Probably invalid screenshot. See error below.");
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}