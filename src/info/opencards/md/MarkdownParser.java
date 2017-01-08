package info.opencards.md;

import info.opencards.core.CardFile;

import java.util.List;

/**
 * It defines methods needed to parse a markdown file into a set of {@link MarkdownFlashcard}.
 * It was added to allow parsing of different markdown file formats.
 */
public interface MarkdownParser {

    /**
     * It parses the mardkdown source file from provided {@link CardFile} into a list of {@link MarkdownFlashcard}
     *
     * @param cardFile file to be parsed
     * @return A list of {@link MarkdownFlashcard}
     */
    List<MarkdownFlashcard> parse(CardFile cardFile);
}
