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
