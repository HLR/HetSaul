# Sentiment analysis

Sentiment Analysis is the process of determining whether a piece of text is positive, negative or neutral. It’s also called opinion mining that is inferring the opinion or attitude of a speaker.
A common use case for this task is to discover how people feel about a particular topic.
For example, you want to find out if people on Twitter are interested in New Orleans seafood restaurants.
Twitter sentiment analysis can answer such a question. There are commercial sentiment analyzers available but here we show how you can write one for yourself using Saul.

As any other learning based program in Saul, the sentiment analyser also has the components of (Readers, DataModel, Classifiers, Applications), described as follows:

## The Reader
The Data for this example comes from two different sources. One is the collected tweets by Stanford university which is stored in csv files with the format described [here](DataFormat.txt),
and is used offline for a standard training and test setting. Each tweet in this collection has been manually annotated by human with one of the three above mentioned labels. There is a Reader program called `TweetReader` that helps you
to read the data items in the collection of tweets.
Second source of data, is the stream of messages that is received from tweeter online and is used to classify the tweets as they arrived. For using this source you will need to
use a twitter client which is also provided in this package.

# Twitter client
For the usage of real time data you need to use a tweeter client that is described [here](TwiterClient.md).
This client includes the instructions and the java codes that enables us to receive real time twitter data and filter it according to our interest using some keywords.
This code is independent from the Saul's learning based programs.

## The Declarations
This part includes Data model declarations and Classifiers declarations.
The [data model](twitterDataModel.scala) contains one type of `node`

```scala
val tweet = node[Tweet]
```

and three types of properties:

 ```scala
    val WordFeatures = property(tweet) ...
     val BigramFeatures = property(tweet) ...
     val Label = property(tweet)...
 ```
The first two properties return the list of words and the list of pairs of words given a piece of text. These properties can be used
as features of the classifiers, later.
The last property returns the sentiment label of the piece of text. This can be used as the unknown label of a tweet to be predicted by trained classifiers.

The [classifier declaration](twitterClassifiers.scala) is very standard.
It includes the specification of the label:

```scala
    def label = Label
    
```    

 and features which are all among properties defined in the data model:

 ```scala
    override def feature = using(WordFeatures, BigramFeatures)
 ```

 in addition to the classification algorithm:

 ```scala
  override lazy val classifier = new SparseNetworkLearner()
  ```

## The applications
We have two applications for this example. One is a program that populates the actual data read from the reader into the data model:

 ```scala
     tweet.populate(TrainReader.tweets.toList)
     tweet.populate(TestReader.tweets.toList, train = false)
 ```

 and then trains and tests the sentiment classifier:

 ```scala
   sentimentClassifier.learn(10)
   sentimentClassifier.test()
 ```

see [here](SentimentApp.scala).

Another App is a one that makes use of the trained classifiers to predict the sentiment of the stream of tweets, [here](twitterStreamApp.scala).



