/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.twitter.tweet;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.Location;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A thin client for accessing Twitter's (filtered) firehose.
 * Using the Hosebird Client twitter library (https://github.com/twitter/hbc).
 * <p/>
 * To use this, you need to get authentication tokens from Twitter,
 * by registering your app here: https://apps.twitter.com
 *
 * @author Christos Christodoulopoulos
 */
public class TwitterClient {

    /**
     * The main message queue: Be sure to size this properly based on expected Tweets-Per-Second of your stream
     */
    private final BlockingQueue<String> msgQueue;
    private final BasicClient client;

    /**
     * Creates a filtered-stream client. Filters can be locations, search terms or languages.
     *
     * @param terms     Search-term filters; see https://dev.twitter.com/streaming/overview/request-parameters#track
     *                  You need at least one of these or a {@code location} as a filter.
     * @param locations {@link Location} filters (bounding boxes of coordinates from: http://boundingbox.klokantech.com ).
     *                  You need at least one of these or a {@code term} as a filter.
     * @param languages Optional: Language filters (https://dev.twitter.com/streaming/overview/request-parameters#language)
     */
    public TwitterClient(List<String> terms, List<Location> locations, List<String> languages) {
        if ((terms == null || terms.isEmpty()) && (locations == null || locations.isEmpty())) {
            System.err.println("You need to define at least one filter (locations or terms)");
            System.exit(-1);
        }
        Properties props = new Properties();
        try {
            props.load(new FileReader("config/auth.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        msgQueue = new LinkedBlockingQueue<>(10000);

        // Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth)
        Hosts hosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();

        if (locations != null && !locations.isEmpty())
            endpoint.locations(locations);

        if (terms != null && !terms.isEmpty())
            endpoint.trackTerms(terms);

        if (languages != null && !languages.isEmpty())
            endpoint.languages(languages);


        // These secrets should be read from a config file
        String consumerKey = props.getProperty("consumerKey");
        String consumerSecret = props.getProperty("consumerSecret");
        String token = props.getProperty("token");
        String secret = props.getProperty("secret");
        Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secret);


        ClientBuilder builder = new ClientBuilder()
                .hosts(hosts)
                .authentication(auth)
                .endpoint(endpoint)
                .processor(new StringDelimitedProcessor(msgQueue));

        client = builder.build();
        // Attempts to establish a connection.
        client.connect();
    }

    public static void main(String[] args) {
        // Set up location filters
        List<Location> locations = Arrays.asList(Locations.URBANA_CHAMPAIGN, Locations.EDINBURGH);
        // Set up search-term filters
        //List<String> terms = Arrays.asList("machine learning", "natural language processing");
        // Set up language filters
        //List<String> languages = Arrays.asList("en", "es");

        TwitterClient client = new TwitterClient(null, locations, null);

        // A separate thread for handling the queue of tweets
        MessageHandler simpleMessageHandler = new SimpleMessageHandler(client.getMsgQueue(), client.getClient());
        Thread thread = new Thread(simpleMessageHandler);
        thread.start();
    }

    public BlockingQueue<String> getMsgQueue() {
        return msgQueue;
    }

    public BasicClient getClient() {
        return client;
    }
}
