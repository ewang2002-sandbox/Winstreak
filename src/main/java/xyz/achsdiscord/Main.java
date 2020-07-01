package xyz.achsdiscord;

import xyz.achsdiscord.request.BedwarsData;
import xyz.achsdiscord.request.HypixelRequest;
import xyz.achsdiscord.request.RequestParser;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        Path path;
        try {
            path = Path.of(System.getProperty("user.home"), "AppData", "Roaming", ".minecraft", "screenshots");
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        System.out.println("[INFO] Starting Service.");
        System.out.println("[INFO] Checking: " + path.toString());
        DirectoryWatcher.startMonitoring(path);
    }
}
