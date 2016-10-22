/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.twitter.datastructures;

import org.json.JSONArray;

/**
 * Represents media elements uploaded with the Tweet.
 *
 * Additional fields/info:
 * https://dev.twitter.com/overview/api/entities#obj-media
 */
public class Medium {
    /** The URL embedded directly into the raw Tweet text. */
    private final String URL;

    /** Type of uploaded media. (e.g. photo) */
    private final String type;

    /**
     * An index pair that represents:
     * <ul>
     * <li>The location of the first character of the URL in the Tweet text. </li>
     * <li>The location of the first non-URL character occurring after the URL
     * (or the end of the string if the URL is the last part of the Tweet text). </li>
     * </ul>
     */
    private final IndexPair index;

    public Medium(JSONArray index, String URL, String type) {
        this.index = new IndexPair(index.getInt(0), index.getInt(1));
        this.URL = URL;
        this.type = type;
    }

    public String getURL() {
        return URL;
    }

    public String getType() {
        return type;
    }

    public IndexPair getIndex() {
        return index;
    }

    public String toString() {
        return getURL() + "(" + getType() + ")" + "[" + getIndex() + "]";
    }
}
