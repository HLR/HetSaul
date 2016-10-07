/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.twitter.datastructures;

import org.json.JSONArray;

/**
 * Represents hashtags which have been parsed out of the Tweet text.
 */
public class Hashtag {
    /**
     * An index pair that represents:
     * <ul>
     * <li>The location of the # character in the Tweet text string. </li>
     * <li>The location of the first character after the hashtag. </li>
     * </ul>
     */
    private final IndexPair index;

    /** Name of the hashtag, minus the leading ‘#’ character. */
    private final String text;

    public Hashtag(JSONArray index, String text) {
        this.index = new IndexPair(index.getInt(0), index.getInt(1));
        this.text = text;
    }

    public IndexPair getIndex() {
        return index;
    }

    public String getText() {
        return text;
    }

    public String toString() {
        return getText() + "[" + getIndex() + "]";
    }
}
