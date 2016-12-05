

#Learning Paradigms

/*Documented by Parisa Kordjamshidi*/

Saul facilitates the flexible design of complex learning models with various configurations.
By complex models we mean the models that aim at prediction of more than one output variable where these outputs might have relationships to each other.
Such models can be designed using the following paradigms,

   * [Local models](#local) trains single classifiers (Learning only models (LO)) each of which learns and predicts a single variable in the output independently.
   * [Constrained conditional models (CCM)](#L+I) for training independent classifiers and using them jointly for global decision making in prediction time. (Learning+Inference (L+I)).
   * [Global models](#IBT) for joint training and joint inference (Inference-Based-Training (IBT)).
   * [Pipeline models](#pipeline) for complex problems where the output of each model is used as the input of the next model (these models are different layers in a pipeline).

The above mentioned paradigms can be tested using this simple badge classifier example, [here](saul-examples/src/main/scala/edu/illinois/cs/cogcomp/saulexamples/Badge/BagesApp.scala).

<a name="local">
##Local models
These models are a set of single classifiers. Each classifier is defined with the `Learnable` construct and is trained and makes prediction independent from other classifiers.
 The `Learnable` construct requires specifying a single output variable, that is, a label which is itself a property in the data model, and the features which is also a
 comma separated list of properties.

 ```scala
 object ClassifierName extends Learnable (node) {
   def label = property1
   def feature = using(property2,property3,...)
   //a comma separated list of properties
 }
 ```

 For the details about the `Learnable` construct see [here](SAULLANGUAGE.md).

<a name="L+I">
##Learning+Inference models
These models are useful for when we need to consider the global relations between the outputs of a bunch of classifiers during the
prediction time. Each classifier is defined with the same `Learnable` construct as a local model. In addition to the Learnable definitions, the programmer
has the possibility to define a number of logical constraints between the output of the Learnables (classifiers).
Having the constraint definitions in place (see [here](SAULLANGUAGE.md) for syntax details), the programmer is able to define
new constrained classifiers that use the Learnables and constraints.

```scala
object ConstrainedClassifierName extends ConstrainedClassifier[local_node_type,global_node_type](LocalClassifier)
 {
   def subjectTo = constraintExpression
   // Logical constraint expression comes here, it defines the relations between the
   // LocalClassifier and other Learnables defined before
 }
 ```
When we use the above `ConstrainedClassifierName` to call test or make predictions, the `LocalClassifier` is used
but the predictions are made in way that `constraintExpression` is hold. There is no limitation for the type of local classifiers.
They can be SVMs, decision trees or any other learning models available in Saul, [here](lbjava/blob/master/lbjava/doc/ALGORITHMS.md)
                                                                                  and [here](saul-core/src/main/java/edu/illinois/cs/cogcomp/saul/learn/SaulWekaWrapper.md).

<a name="IBT">
##Inference-based Learning
For the inference based models the basic definitions are exactly similar to the L+I models. In other words, the programmer
just needs to define the `Learnables` and `ConstrainedClassifiers`. However, to train the ConstrainedClassifiers jointly, instead of
training local classifiers independently, there are a couple of joint training functions that can be called.
These functions receive the list of ConstrainedClassifiers as input and train their parameters jointly. In contrast to
L+I models here the local classifiers can be defined as `SparsePerceptron`s or `SparseNetworkLearner`s only. This is because the
joint training should have its own strategy for the wight updates of the involved variables (those variables come down to be the outputs of the local classifiers here).
For the two cases the programmer can use

```scala
 JointTrainSparseNetwork.train(param1,param2,...) /* a list of parameters go here*/
 JointTrainSparsePerceptron.train(param1,param2,...) /*a list of parameters here*/
```

For example,

 ```scala
  JointTrainSparseNetwork.train(badge, cls, 5, init = true, lossAugmented = true)
 ```

The list of parameters are the following:

- param1: The name of a global node in the data model that itself or the connected nodes to it are used by the involved `Learnable`s.

- param2: The collection of ```ConstainedClassifier```s

- param3: The number of iterations over the training data.

- param4: If the local classifiers should be cleaned from the possibly pre-trained values and initialized by zero weights, this parameter should be true.

- param5: If the approach uses the loss augmented objective for making inference, see below for description.

###Basic approach

The basic approach for training the models jointly is to do a global prediction at each step of the training and if the
predictions are wrong update the weights of the related variables.

###Loss augmented

The loss-augmented approach adds the loss of the prediction explicitly to the objective of the training and finds the most violated output per each training example;
it updates the weights of the model according to the errors made in the prediction of the most violated output.
This approach minimizes a convex upper bound of the loss function and has been used in structured SVMs and Structured Perceptrons.
 However, considering an arbitrary loss in the objective will make complexities in the optimization, therefore in the implemented version, here, we assume the loss is decomposed similar to
feature function. That is, the loss is a hamming loss defined per classifier. The loss of the whole structured output is computed by the weighted sum of
 the loss of its components. For exmaple if there are two variables `c1` and `c2` in the output with corresponding predictions `cp1` and `cp2` then  the loss will be
 `[hamming(c1,cp1)/2+hamming(c2,cp2)/2]`. The weight of each component's loss is `1/numberOfOutputVariables` by default.

 In Saul, the programmer can indicate if he/she needs to consider this global hamming loss in the objective or not. And this can be done by passing
 the above mentioned `param5` as true in the `JointTrainingSparseNetwork` algorithm.
 An example of this usage can be seen [here](saul-examples/src/main/scala/edu/illinois/cs/cogcomp/saulexamples/Badge/BagesApp.scala#L64).

<a name="pipeline">
##Pipelines

Building pipelines is naturally granted in Saul. The programmer can simply define properties that are the predictions of
the classifiers and use those outputs as the input of other classifiers by mentioning them in the list of the properties in the below construct when defining the
pipeline classifiers,

```scala
   def feature = using(/*list of properties including the prediction of other classifiers.*/)
```

Here is a more complete example which passes the output of the `ClassifierLayer1` to the input of the `ClassifierLayer2`:

   ```scala
     object ClassifierLayer1 extends Learnable (node) {
       def label = labelProperty1
       def feature = using(property2, property3,...)
     }
     object ClassifierLayer2 extends Learnable (node) {
       def label = labelProperty2
       def feature = using(classifier1Labels, ,...) // using the prediction of the classifier in the previous layer
     }
   ```

This will be defined in data-model object:

   ```scala
    val classifier1Labels = new Property(node){  x: Type => ClassifierLayer1(x)  }
   ```

See [here](saul-examples/src/main/scala/edu/illinois/cs/cogcomp/saulexamples/Badge/BadgeClassifiers.scala#L43) for a working example.




