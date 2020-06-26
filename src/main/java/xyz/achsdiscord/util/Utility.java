package xyz.achsdiscord.util;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.utils.AttachmentOption;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class Utility {
    public static String getPathOfResource(String resource) throws URISyntaxException {
        URL fileUrl = Utility.class.getClassLoader().getResource(resource);
        return new File(fileUrl.toURI()).getPath();
    }

    public static MessageAction sendImage(MessageChannel channel, BufferedImage img) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(img, "png", bytes);
        bytes.flush();
        return channel.sendFile(bytes.toByteArray(), "result.png", new AttachmentOption[] { });
    }
}
