# Sentiment analysis

Sentiment Analysis is the process of determining whether a piece of text is positive, negative or neutral. It’s also called opinion mining that is inferring the opinion or attitude of a speaker.
A common use case for this task is to discover how people feel about a particular topic.
For example, you want to find out if people on Twitter are interested in New Orleans seafood restaurants.
Twitter sentiment analysis can answer such a question. There are commercial sentiment analyzers available but here we show how you can write one for yourself using Saul.

As any other learning based program in Saul, the sentiment analyser also has the following components:

## The Reader
The Data for this example comes from two different sources. One is the collected tweets by Stanford university which is stored in csv files with the format described [here](DataFormat.txt),
and is used offline for a standard training and test setting. Second is the stream of the data that is received from tweeter online and is used to classify the tweets as they arrived.

# Twitter client
For the second real time data we need to use a tweeter client that is described [here](src/main/scala/edu/illinois/cs/cogcomp/saulexamples/nlp/TwitterSentimentAnalysis/TwiterClient.md).
This includes the java codes that enables us to receive real time twitter data and filter it according to our interest using some keywords. This code is independent from the Saul's learning based programs.

## The Declarations
This part includes Data model declarations and Classifiers declarations.
The [data model](src/main/scala/edu/illinois/cs/cogcomp/saulexamples/nlp/TwitterSentimentAnalysis/twitterDataModel.scala) contains one type of `node`

```  val tweet = node[Tweet]```

and three types of properties:

 ``` val WordFeatures = property(tweet) ...
     val BigramFeatures = property(tweet) ...
     val Label = property(tweet) ...
 ```
The first two properties return the list of words and the list of pairs of words given a piece of text. These properties can be used
as features of the classifiers, later.
The last property returns the sentiment label of the piece of text. This can be used as the unknown label of a tweet to be predicted by trained classifiers.

The [classifier declaration](src/main/scala/edu/illinois/cs/cogcomp/saulexamples/nlp/TwitterSentimentAnalysis/twitterClassifiers.scala) is very standard.
It includes the specification of the label:
 ```def label = Label```

 and features which are all among properties defined in the data model:

 ```   override def feature = using(WordFeatures, BigramFeatures)```

 in addition to the classification algorithm:

 ``` override lazy val classifier = new SparseNetworkLearner() ```

## The applications
We have two applications for this example. One is a program that populates the actual data read from the reader into the data model:

 ``` tweet.populate(TrainReader.tweets.toList)
     tweet.populate(TestReader.tweets.toList, train = false)```

 and then trains and tests the sentiment classifier:

 ``` sentimentClassifier.learn(10)
   sentimentClassifier.test()```

see [here](src/main/scala/edu/illinois/cs/cogcomp/saulexamples/nlp/TwitterSentimentAnalysis/SentimentApp.scala).

Another App is a one that makes use of the trained classifiers to predict the sentiment of the stream of tweets, [here](twitterStreamApp.scala).



