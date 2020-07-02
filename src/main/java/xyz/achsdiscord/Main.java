package xyz.achsdiscord;

import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int brokenBeds = -1;
        int finalKills = -1;
        int amtTryHards = -1;

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("How many broken beds does a tryhard has? ");
                brokenBeds = scanner.nextInt();
                if (brokenBeds > 0) {
                    break;
                }
            }

            while (true) {
                System.out.println("How many final kills does a tryhard has? ");
                finalKills = scanner.nextInt();
                if (finalKills > 0) {
                    break;
                }
            }

            while (true) {
                System.out.println("How many total tryhards in lobby before we recommend that you leave?");
                amtTryHards = scanner.nextInt();
                if (amtTryHards > 0) {
                    break;
                }
            }
        }

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
        DirectoryWatcher.startMonitoring(path, finalKills, brokenBeds, amtTryHards);
    }
}
