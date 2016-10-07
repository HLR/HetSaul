/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.twitter.tweet;

import com.twitter.hbc.httpclient.BasicClient;

import java.util.concurrent.BlockingQueue;

public abstract class MessageHandler implements Runnable {
    protected final BlockingQueue<String> msgQueue;
    protected final BasicClient client;

    public MessageHandler(BlockingQueue<String> msgQueue, BasicClient client) {
        this.msgQueue = msgQueue;
        this.client = client;
    }

    abstract public void run();
}
