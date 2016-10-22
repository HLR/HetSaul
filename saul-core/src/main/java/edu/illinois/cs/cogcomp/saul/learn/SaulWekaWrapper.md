#Saul Weka Wrapper

The `SaulWekaWrapper` class wraps [Weka learners](http://www.cs.waikato.ac.nz/ml/weka/) in order to enable Saul to use them.
This class, converts the feature vectors that Saul generates to compatible instances of Weka, and then calls Weka to build the learner using these data.

**Note**: It is crucial to note that WEKA learning algorithms do not learn online. Therefore, whenever the learn method of the SaulWekaWrapper is called, no learning actually takes place. Rather, the input object is added to a collection of examples for the algorithm to learn once the doneLearning() method is called.

## Usage
The syntax is very similar to what is used for Saul's native learners. For example here is the syntax to use Weka's Naive Bayes learner:

```scala
object SpamClassifierWeka extends Learnable[Document](docs) {
 def label = spamLabel
 override lazy val classifier = new SaulWekaWrapper(new weka.classifiers.bayes.NaiveBayes())
 override def feature = using(wordFeature)
}
```

## Algorithms

There are a wide variety of learners in Weka. Here is some if those learners:

###Decision Trees
You can find this category of learners at `weka.classifiers.trees` package of weka.
Here is few of them: 

* [`ID3`](http://weka.sourceforge.net/doc.stable/weka/classifiers/trees/Id3.html) : One of the early decision tree algorithms
* [`J48`](http://weka.sourceforge.net/doc.dev/weka/classifiers/trees/J48.html) : a java implementation of C4.5 algorithm
* [`NBTree`](http://weka.sourceforge.net/doc.packages/naiveBayesTree/weka/classifiers/trees/NBTree.html) : decision tree with naive Bayes classifiers at the leaves
* [`RandomForest`](http://weka.sourceforge.net/doc.dev/weka/classifiers/trees/RandomForest.html) : Random forests algorithm

###Famous classifiers
These algorithms are located at `weka.classifiers.functions` package.

* [`SMO`](http://weka.sourceforge.net/doc.dev/weka/classifiers/functions/SMO.html): a fast implementation of SVM using *Sequential Minimal Optimization*
* [`LibSvm`](http://weka.sourceforge.net/doc.stable/weka/classifiers/functions/LibSVM.html): LibSvm implementation of SVM
* [`MultilayerPerceptron`](http://weka.sourceforge.net/doc.dev/weka/classifiers/functions/MultilayerPerceptron.html): MLP neural network that uses backpropagation algorithm for learning.
* [`RBFNetwork`](http://weka.sourceforge.net/doc.stable/weka/classifiers/functions/RBFNetwork.html): a RBF network
* [`SimpleLogistic`](http://weka.sourceforge.net/doc.dev/weka/classifiers/functions/SimpleLogistic.html)

###Rule Based 

* [`ConjunctiveRule`](http://weka.sourceforge.net/doc.packages/conjunctiveRule/weka/classifiers/rules/ConjunctiveRule.html)
* [`DecisionTable`](http://weka.sourceforge.net/doc.dev/weka/classifiers/rules/DecisionTable.html) : Class for building and using a simple decision table majority classifier.
* [`DTNB`](http://weka.sourceforge.net/doc.packages/DTNB/weka/classifiers/rules/DTNB.html) : Class for building and using a decision table/naive bayes hybrid classifier.
* [`JRip`](http://weka.sourceforge.net/doc.stable/weka/classifiers/rules/JRip.html)This class implements a propositional rule learner, Repeated Incremental Pruning to Produce Error Reduction (RIPPER), which was proposed by William W. Cohen as an optimized version of IREP.
* [`Prism`](http://weka.sourceforge.net/doc.packages/simpleEducationalLearningSchemes/weka/classifiers/rules/Prism.html) : Class for building and using a PRISM rule set for classification.

###Ensemble methods
This kind of classifiers can be found at `weka.classifiers.meta` package.
 
* [`AdaBoostM1`](http://weka.sourceforge.net/doc.dev/weka/classifiers/meta/AdaBoostM1.html) : Class for boosting a nominal class classifier using the Adaboost M1 method. 
* [`Bagging`](http://weka.sourceforge.net/doc.stable/weka/classifiers/meta/Bagging.html) : Class for bagging a classifier to reduce variance.
* [`RandomCommittee`](http://weka.sourceforge.net/doc.dev/weka/classifiers/meta/RandomCommittee.html) : Class for building an ensemble of randomizable base classifiers.
* [`Dagging`](http://weka.sourceforge.net/doc.packages/dagging/weka/classifiers/meta/Dagging.html) : This meta classifier creates a number of disjoint, stratified folds out of the data and feeds each chunk of data to a copy of the supplied base classifier. Predictions are made via majority vote, since all the generated base classifiers are put into the Vote meta classifier.
 
###Lazy classifiers
This type of classifiers are located in `weka.classifiers.lazy` package.

* [`IB1`](http://weka.sourceforge.net/doc.stable/weka/classifiers/lazy/IB1.html) : Nearest-neighbour classifier.
* [`IBk`](http://weka.sourceforge.net/doc.dev/weka/classifiers/lazy/IBk.html) : K-nearest neighbours classifier. 
* [`KStar`](http://weka.sourceforge.net/doc.dev/weka/classifiers/lazy/KStar.html) : K* is an instance-based classifier, that is the class of a test instance is based upon the class of those training instances similar to it, as determined by some similarity function. It differs from other instance-based learners in that it uses an entropy-based distance function.
* [`LBR`](http://weka.sourceforge.net/doc.stable/weka/classifiers/lazy/LBR.html) : Lazy Bayesian Rules Classifier.
* [`LWL`](http://weka.sourceforge.net/doc.dev/weka/classifiers/lazy/LWL.html): Locally weighted learning.
