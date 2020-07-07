package xyz.achsdiscord.request;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.Callable;

class PlanckeAPIRequester implements Callable<String> {
    private final String _name;

    /**
     * Creates a new PlanckeAPIRequester object. One object should be created per thread.
     * @param name The name to check.
     */
    public PlanckeAPIRequester(String name) {
        this._name = name;
    }

    /**
     * Makes a request to Plancke's website, which is simply a public stats browser for all players in Hypixel. I chose to use Plancke's website instead of making a call directly to Hypixel's API because of the possibility of a ban.
     *
     * @return The raw HTML output of the website.
     * @throws IOException Thrown if something is wrong with the output.
     */
    public String call() throws IOException {
        final String FINAL_URL = "https://plancke.io/hypixel/player/stats/" + this._name;

        URL url = new URL(FINAL_URL);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.addRequestProperty("X-Forwarded-For", generateRandomIpAddress());
        // player not found
        if (connection.getResponseCode() == 404) {
            return "";
        }
        StringBuilder builder;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            builder = new StringBuilder();
            String str;
            while ((str = in.readLine()) != null) {
                builder.append(str).append(System.lineSeparator());
            }
        }
        return builder.toString();
    }

    /**
     * Generates a random IP address.
     *
     * @return A random IP address.
     */
    private String generateRandomIpAddress() {
        Random r = new Random();
        return r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
    }
}
