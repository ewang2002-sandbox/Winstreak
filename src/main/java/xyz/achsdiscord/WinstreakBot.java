package xyz.achsdiscord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import xyz.achsdiscord.classes.Player;
import xyz.achsdiscord.util.Utility;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public final class WinstreakBot {
    static {
        loggedPlayers = new HashMap<>();
    }

    public static WinstreakBot instance;
    public static Map<String, Player> loggedPlayers;

    private String _token;
    private JDA _botClient;

    public WinstreakBot() {
        WinstreakBot.instance = this;
    }

    public WinstreakBot setToken(String token) {
        this._token = token;
        return this;
    }

    public void login() {
        try {
            this._botClient = JDABuilder
                    .createDefault(this._token)
                    .build();
        }
        catch (LoginException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }

    // no idea if we need this or not
    public static File getPathOfJar() throws URISyntaxException {
        return new File(WinstreakBot
                .class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI());
    }

    public static void main(String[] args) throws FileNotFoundException, URISyntaxException {
        WinstreakBot bot = new WinstreakBot();
        Scanner scanner = new Scanner(new File(Utility.getPathOfResource("token.txt")));
        StringBuilder token = new StringBuilder();
        while (scanner.hasNextLine()) {
            token.append(scanner.nextLine());
        }

        scanner.close();

        bot.setToken(token.toString())
                .login();
    }
}
