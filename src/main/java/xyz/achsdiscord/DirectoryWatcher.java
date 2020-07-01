package xyz.achsdiscord;

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

        // continuously check folder
        for (;;) {
            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    List<String> names;
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

                            long startRequest = System.nanoTime();
                            String data = processNames(names);
                            long endRequest = System.nanoTime();
                            System.out.println("[INFO] Requested all data in " + ((endRequest - startRequest) * 1e-9) + " seconds.");
                            System.out.println(data);
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

    public static String processNames(List<String> names) throws ExecutionException, InterruptedException {
        FutureTask<String>[] nameResponses = new FutureTask[names.size()];
        for (int i = 0; i < names.size(); i++) {
            HypixelRequest req = new HypixelRequest(names.get(i));
            nameResponses[i] = new FutureTask<>(req);

            Thread t = new Thread(nameResponses[i]);
            t.start();
        }

        StringBuilder b = new StringBuilder();
        for (int i = 0; i < nameResponses.length; i++) {
            BedwarsData data = new RequestParser(names.get(i), nameResponses[i].get())
                    .parse()
                    .getTotalDataInfo();
            b.append("⇒ ").append(names.get(i)).append(" | Beds: ").append(data.bedsBroken).append(" | Final Kills: ").append(data.finalKillDeathRatio)
                    .append(System.lineSeparator());
        }
        return b.toString();
    }
}