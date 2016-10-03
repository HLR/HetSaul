package edu.illinois.cs.cogcomp.saulexamples.twitter.datastructures;

import org.json.JSONArray;

/**
 * Represents other Twitter users mentioned in the name of the Tweet.
 *
 * Additional fields/info:
 * https://dev.twitter.com/overview/api/entities#obj-usermention
 */
public class Mention {
    /**
     * An index pair that represents:
     * <ul>
     * <li>The location of the ‘@’ character of the user mention. </li>
     * <li>The location of the first non-screenname character following the user mention. </li>
     * </ul>
     */
    private final IndexPair index;

    /** Display name of the referenced user. */
    private final String name;

    /** Screen name of the referenced user. */
    private final String screenName;

    public Mention(JSONArray index, String name, String screenName) {
        this.index = new IndexPair(index.getInt(0), index.getInt(1));
        this.name = name;
        this.screenName = screenName;
    }

    public IndexPair getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String toString() {
        return getName() + "(" + getScreenName() + ")" + "[" + getIndex() + "]";
    }
}
