/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
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
