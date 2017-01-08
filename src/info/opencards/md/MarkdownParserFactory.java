package info.opencards.md;

/**
 * It creates the selected {@link MarkdownParser}
 */
public class MarkdownParserFactory {

    public static MarkdownParser create() {
        return new DefaultMarkdownParser();
    }

}
