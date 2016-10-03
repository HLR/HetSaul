package edu.illinois.cs.cogcomp.saulexamples.twitter.tweet;

import com.twitter.hbc.httpclient.BasicClient;
import edu.illinois.cs.cogcomp.lbjava.classify.Classifier;
import edu.illinois.cs.cogcomp.saulexamples.twitter.datastructures.Tweet;
import org.json.JSONObject;

import java.util.concurrent.BlockingQueue;

/**
 * An example class that handles the message queue, and runs a classifier on each of the tweets.
 */
public class ClassifierMessageHandler extends MessageHandler {
    private final Classifier classifier;

    public ClassifierMessageHandler(BlockingQueue<String> msgQueue, BasicClient client, Classifier classifier) {
        super(msgQueue, client);
        this.classifier = classifier;
    }

    public void run() {
        while (!client.isDone()) {
            try {
                String msg = msgQueue.take();
                Utils.printInfo(new JSONObject(msg));
                String text = Utils.getCleanText(new JSONObject(msg));
                // Need to convert the text to a Tweet datastructure for the LBJava classifer to work
                String decision = classifier.discreteValue(new Tweet(text));
                System.out.println("\t***Sentiment classification: " + decision);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        client.stop();
    }
}
