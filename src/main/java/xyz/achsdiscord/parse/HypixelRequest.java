package xyz.achsdiscord.parse;

import java.util.concurrent.Callable;

public class HypixelRequest implements Callable<String> {
    private String _name;

    public HypixelRequest(String name) {
        this._name = name;
    }
    public String call() {

        return "";
    }
}
