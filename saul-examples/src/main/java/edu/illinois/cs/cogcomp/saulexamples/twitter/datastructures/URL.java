package edu.illinois.cs.cogcomp.saulexamples.twitter.datastructures;

import org.json.JSONArray;

/**
 * Represents hashtags which have been parsed out of the Tweet url.
 */
public class URL {
    /**
     * An index pair that represents:
     * <ul>
     * <li>The location of the first character of the URL in the Tweet text. </li>
     * <li>The location of the first non-URL character after the end of the URL. </li>
     * </ul>
     */
    private final IndexPair index;

    /** Name of the hashtag, minus the leading ‘#’ character. */
    private final String url;

    public URL(JSONArray index, String url) {
        this.index = new IndexPair(index.getInt(0), index.getInt(1));
        this.url = url;
    }

    public IndexPair getIndex() {
        return index;
    }

    public String getUrl() {
        return url;
    }

    public String toString() {
        return getUrl() + "[" + getIndex() + "]";
    }
}
