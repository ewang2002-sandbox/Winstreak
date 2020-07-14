package xyz.achsdiscord.dirwatcher.tests;

import xyz.achsdiscord.parse.Constants;
import xyz.achsdiscord.parse.exception.InvalidImageException;
import xyz.achsdiscord.parse.v1.AbstractNameParser;
import xyz.achsdiscord.parse.v1.InGameNameParser;
import xyz.achsdiscord.parse.v1.LobbyNameParser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DirectoryV1 {
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

        List<String> allNames = names.getPlayerNames();
        long endImageProcessing = System.nanoTime();

        double imageProcessingTime = (endImageProcessing - startImageProcessing) * 1e-9;

        System.out.println("[INFO] Total: " + allNames.size());
        System.out.println("[INFO] All Names: " + allNames.toString());
        System.out.println("[INFO] Image Processing Time: " + imageProcessingTime + " SEC.");

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

        Map<Constants.TeamColors, List<String>> teammates = names.getPlayerNames();
        long endImageProcessing = System.nanoTime();

        double imageProcessingTime = (endImageProcessing - startImageProcessing) * 1e-9;

        for (Map.Entry<Constants.TeamColors, List<String>> entry : teammates.entrySet()) {
            System.out.println(entry.toString());
        }


        System.out.println("[INFO] Image Processing Time: " + imageProcessingTime + " SEC.");
        System.out.println("=====================================");
    }
}