package info.opencards.core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * A cache for instantiated <code>CardFile</code>s.
 *
 * @author Holger Brandl
 *         <p/>
 */
public class CardFileCache {


    private static final Map<File, CardFile> cache = new HashMap<File, CardFile>();


    public static CardFile getCardFile(File file) {
        if (file == null)
            return null;

        if (!cache.containsKey(file))
            cache.put(file, new CardFile(file));


        return cache.get(file);
    }


    /**
     * Adds an already instantiated <code>CardFile</code> to this cache.
     */
    public static void register(CardFile cardFile) {
        cache.put(cardFile.getFileLocation(), cardFile);
    }
}
