package xyz.achsdiscord.request;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class MultipleRequestHandler {
    private final List<String> _names;

    /**
     * Creates a new MultipleRequestHandler object. This class is primarily designed to
     * send multiple requests to Plancke's API through multithreading.
     * @param names The list of names to get data for.
     */
    public MultipleRequestHandler(List<String> names) {
        this._names = names;
    }

    /**
     * Sends requests to Plancke's API. Each name will be given its own thread,
     * which will be used to send a request to the API and get data.
     *
     * @return A Map containing the names of users and the data. 
     */
    public Map<String, String> sendRequests() throws ExecutionException, InterruptedException {
        Map<String, String> data = new ConcurrentHashMap<>();
        FutureTask[] nameResponses = new FutureTask[this._names.size()];
        for (int i = 0; i < this._names.size(); i++) {
            PlanckeAPIRequester req = new PlanckeAPIRequester(this._names.get(i));
            nameResponses[i] = new FutureTask<>(req);

            Thread t = new Thread(nameResponses[i]);
            t.start();
        }

        for (int i = 0; i < nameResponses.length; i++) {
            data.put(this._names.get(i), (String) nameResponses[i].get());
        }

        return data;
    }
}
