package xyz.achsdiscord.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class Utility {

    public static String getPathOfResource(String resource) throws URISyntaxException {
        URL fileUrl = Utility.class.getClassLoader().getResource(resource);
        assert fileUrl != null;
        return new File(fileUrl.toURI()).getPath();
    }
}
