package xyz.achsdiscord.request;

import java.util.concurrent.Callable;

public class HypixelRequest implements Callable<String> {
    private final String _name;

    public HypixelRequest(String name) {
        this._name = name;
    }

    public String call() {

        return "";
    }
}
