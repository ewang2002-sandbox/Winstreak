package xyz.achsdiscord.classes;

public class Player {
    private String _username;
    private int _destroyedBeds;
    private int _finalKills;

    public Player(String username, int destroyedBeds, int finalKills) {
        this._username = username;
        this._destroyedBeds = destroyedBeds;
        this._finalKills = finalKills;
    }

    public String getUsername() {
        return this._username;
    }

    public int getDestroyedBeds() {
        return this._destroyedBeds;
    }

    public int getFinalKills() {
        return this._finalKills;
    }
}
