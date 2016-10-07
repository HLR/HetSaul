/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.twitter.datastructures;

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier;

import java.util.Arrays;
import java.util.List;

/**
 * A simple data structure that holds all the information used by the LBJava {@link Classifier}.
 * <p/>
 * Can be expanded to include all of the information in Twitter's JSON. See lbj/classifier.lbj for more information.
 */
public class Tweet {

    String text, sentimentLabel;

    /**
     * Data file format has 6 fields:
     0 - the polarity of the tweet (0 = negative, 2 = neutral, 4 = positive)
     1 - the id of the tweet (2087)
     2 - the date of the tweet (Sat May 16 23:58:44 UTC 2009)
     3 - the query (lyx). If there is no query, then this value is NO_QUERY.
     4 - the user that tweeted (robotickilldozr)
     5 - the text of the tweet (Lyx is cool)
     * @param csvFields The comma-separated line containing the 6 fields
     */
    public Tweet(String[] csvFields) {
        if (csvFields.length < 6) {
            System.err.println("Data doesn't conform to the formatting standard:\n" + Arrays.toString(csvFields));
            System.exit(-1);
        }
        sentimentLabel = stripQuotes(csvFields[0]);
        text = stripQuotes(csvFields[5]);
    }

    /**
     * Constructor for testing. Only text is required (the label is inferred).
     * @param text The text of the tweet
     */
    public Tweet(String text) {
        this.text = text;
        sentimentLabel = null;
    }

    public List<String> getWords() {
        return Arrays.asList(text.split("\\s+"));
    }

    public String getSentimentLabel() {
        return (sentimentLabel.equals("0")) ? "negative" : "positive";
    }

    private String stripQuotes(String str) {
        return str.replaceAll("\"", "");
    }
}
