
##Saul Workflow
A Saul programmer needs to think about doing this workflow.

1) Prepare a Reader program written in Java or Scala to read the data in some Java or Scala collections of objects.

2) Create a datamodel object by opening a new file and input your declaration in this template ([see more](DATAMODELING.md)):

 ```scala
   object myDataModelName extends DataModel {
        //the definition of nodes, edges and properties come here!
   }
  ```

3) Create an object for defining your classifiers, for a simple standard machine learning task you
will probably have only one classifier to define. This will be in such a template ([see more](SAULLANGUAGE.md)):

```scala
object allMyClassifiers {
    object myClassifierName1 extends Learnable(myNode){
         //the definition of features, label and algorithm comes here!
 }
 ...
```

4) For the advanced usage of Saul when you deal with predicting structured output and using constraints you open another object and define
your global constraints in there in the following template (for the classic machine learning usage you can skip this step): ([see more](SAULLANGUAGE.md))

 ```scala
 object allMyConstraints {
     val myFirstConstraintName = ConstrainedClassifier.constraint[ObjectType] {
      x: ObjectType =>
     //myLogicalExpression that describes my constrain comes here!
   }
```

5) Having the above items in place, now you can write your application that loads the data (population of the data into the data model) trains over the data, tests the models
makes predictions or whatever you need in your learning based program, this is a typical scenario for a main app that uses classifiers ([see more](CONCEPTUALSTRUCTURE.md)):

```scala
object myMainApplication extends App {
     // call your reader here and read the data in some collection
     // populate you collection into the data model
     // call train over your defined classifiers or load a pre-trained model into your classifier
     // call test to evaluate your classifiers or use your classifier for other prediction purposes
}
```

6) Now the `myMainApplication` is your runnable object, just right click on it and press run to see the outcome.

You can see all these 5 steps or at least 4 of them (when there is no constraints) in our saul example package for every example.
