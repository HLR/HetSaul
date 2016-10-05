
# illinois-twitter-client

A thin client for accessing Twitter's (filtered) firehose. Uses the [Hosebird Client twitter library](https://github.com/twitter/hbc).

## Requirements
To use the client, you need to get authentication tokens from Twitter, by registering your app [here](https://apps.twitter.com).
 - Go to the above link and press the `Creat New App` button.
 - Fill in the form about your new application.

Once you have your tokens, store them in `config/auth.properties` using the following keys:

    consumerKey = 1234key
    consumerSecret = 1234secret
    token = 1234token
    secret = 1234secret

## Running
A typical scenario involves instantiating the `TwitterClient` with some filters (locations, search terms, languages)
and creating a `MessageHandler` thread that can access the queue of messages coming from the stream.

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

`ClassifierMessageHandler` is another example of `MessageHandler` where a `Classifier` is called on
every message in the stream.
The folder `data` contains sample sentiment annotated data from [here](http://cs.stanford.edu/people/alecmgo/trainingandtestdata.zip).

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