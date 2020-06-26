package xyz.achsdiscord.events;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotReadyEvent extends ListenerAdapter {
    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("I am ready!");
    }
}