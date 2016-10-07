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
You can use the `SimpleMessageHandler` and the `main` app that calls it as a template and write your own `MessageHandler`
which manipulates the received messages from the stream. For the goal of `Saul` we want the classifiers to be applied on the messages from the queue and
classify them, here with a sentiment label. Therefor we have prepared a `ClassifierMessageHandler` which receives a classifier as an input parameter and applies it on each individual message:

 ```
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
```

This `ClassifierMessageHandler` can be left unchanged as it is in [here](src/main/java/edu/illinois/cs/cogcomp/saulexamples/twitter/tweet/ClassifierMessageHandler.java). However, you can work on the Saul side of the Learning based programs and improve
your classifiers, use better models and pass them to the same module here.