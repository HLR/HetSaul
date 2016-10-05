package edu.illinois.cs.cogcomp.saulexamples.twitter.tweet;

import com.twitter.hbc.httpclient.BasicClient;
import org.json.JSONObject;

import java.util.concurrent.BlockingQueue;

/**
 * An example class that handles the message queue, to be run as separate thread to ensure good performance.
 */
public class SimpleMessageHandler extends MessageHandler {

    public SimpleMessageHandler(BlockingQueue<String> msgQueue, BasicClient client) {
        super(msgQueue, client);
    }

    public void run() {
        while (!client.isDone()) {
            try {
                String msg = msgQueue.take();
                Utils.printInfo(new JSONObject(msg));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        client.stop();
    }
}
