package xyz.achsdiscord.parse;

import java.util.List;

public interface INameParser {
    INameParser cropImageIfFullScreen() throws InvalidImageException;
    INameParser makeBlackAndWhiteAndGetWidth();
    INameParser cropHeaderAndFooter() throws InvalidImageException;
    INameParser fixImage() throws InvalidImageException;
    List<String> getPlayerNames();
    List<String> getPlayerNames(List<String> exempt);
}
