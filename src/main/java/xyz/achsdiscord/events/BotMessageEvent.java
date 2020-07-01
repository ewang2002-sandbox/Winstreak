package xyz.achsdiscord.events;

import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.achsdiscord.parse.InvalidImageException;
import xyz.achsdiscord.parse.NameProcessor;
import xyz.achsdiscord.util.Utility;

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
                    // start processing
                    long timeStart = System.nanoTime();
                    var nameProcessor = new NameProcessor(new URL(attachment.getUrl()));
                    long timeA = System.nanoTime();
                    nameProcessor.cropImageIfFullScreen()
                            .makeBlackAndWhiteAndGetWidth()
                            .cropHeaderAndFooter()
                            .fixImage();
                    List<String> np = nameProcessor.getPlayerNames();
                    long endTime = System.nanoTime();
                    // end processing

                    StringBuilder b = new StringBuilder();
                    for (int i = 0; i < np.size(); i++) {
                        b.append(np.get(i) + ", ");
                    }
                    Utility.sendImage(event.getChannel(), nameProcessor.getImage()).queue();
                    System.out.println(np.size() + " Players Accounted: " + b.toString());
                    System.out.println("Everything Took: " + ((endTime - timeStart) * 1e-9) + " Seconds");
                    System.out.println("Instantiation Took: " + ((timeA - timeStart) * 1e-9) + " Seconds");
                    System.out.println("Processing Took: " + ((endTime - timeA) * 1e-9) + " Seconds");
                    System.out.println("===========================");
                } catch (IOException | InvalidImageException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
