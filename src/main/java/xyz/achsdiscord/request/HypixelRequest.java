package xyz.achsdiscord.request;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.Callable;

public class HypixelRequest implements Callable<String> {
    private final String _name;

    public HypixelRequest(String name) {
        this._name = name;
    }

    public String call() throws IOException {
        final String FINAL_URL = "https://plancke.io/hypixel/player/stats/" + this._name;

        URL url = new URL(FINAL_URL);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.addRequestProperty("X-Forwarded-For", generateRandomIpAddress());
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder builder = new StringBuilder();
        String str;
        while ((str = in.readLine()) != null) {
            builder.append(str)
                    .append(System.lineSeparator());
        }
        return builder.toString();
    }

    private String generateRandomIpAddress() {
        Random r = new Random();
        return r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
    }
}
