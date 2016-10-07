/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.twitter.tweet;

import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.lbjava.parse.Parser;
import edu.illinois.cs.cogcomp.saulexamples.twitter.datastructures.Tweet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TweetReader {
    public List<Tweet> tweets;
    //int currentTweet;

    public TweetReader(String dataFile) {
        tweets = new ArrayList<>();
        List<String> lines = null;
        try {
            lines = LineIO.readGZip(dataFile);
        } catch (IOException e) {
            System.err.println("Unable to read " + dataFile + ". Exiting...");
            System.exit(-1);
        }
        for (String line : lines) {
            if (!line.isEmpty())
                tweets.add(new Tweet(line.split(",")));
        }
    }

//    @Override
//    public Object next() {
//        if (currentTweet >= tweets.size())
//            return null;
//        return tweets.get(currentTweet++);
//    }
//
//    @Override
//    public void reset() {
//        currentTweet = 0;
//    }
//
//    @Override
//    public void close() {}
}
