package xyz.achsdiscord.events;

import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.achsdiscord.parse.InvalidImageException;
import xyz.achsdiscord.parse.NameProcessor;
import xyz.achsdiscord.util.Utility;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class BotMessageEvent extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        final List<Attachment> attachments = event.getMessage().getAttachments();
        for (Attachment attachment : attachments) {
            if (attachment.isImage()) {
                try {
                    var x = new NameProcessor(new URL(attachment.getUrl()))
                            .fixImage()
                            .cropImage();
                    Utility.sendImage(event.getChannel(), x.getImage()).queue();
                    List<String> a = x.getPlayerNames();
                    System.out.println("Size -> " + a.size());
                    for (String p : a) {
                        System.out.println(p);
                    }
                } catch (IOException | InvalidImageException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
