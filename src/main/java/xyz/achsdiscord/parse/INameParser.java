package xyz.achsdiscord.parse;

import java.util.List;

public interface INameParser {
    INameParser cropImageIfFullScreen() throws InvalidImageException;
    INameParser adjustColorsAndIdentifyWidth();
    INameParser cropHeaderAndFooter() throws InvalidImageException;
    INameParser fixImage() throws InvalidImageException;
    Object getPlayerNames();
    Object getPlayerNames(List<String> exempt);
}
