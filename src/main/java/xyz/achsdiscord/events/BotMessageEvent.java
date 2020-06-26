package xyz.achsdiscord.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.AttachmentOption;
import xyz.achsdiscord.parse.NameProcessor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
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
                    NameProcessor processor = new NameProcessor(new URL(attachment.getUrl()))
                            .fixImage();
                    BufferedImage image = processor.getImage();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
