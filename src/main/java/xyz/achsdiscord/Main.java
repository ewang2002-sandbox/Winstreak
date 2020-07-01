package xyz.achsdiscord;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
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
