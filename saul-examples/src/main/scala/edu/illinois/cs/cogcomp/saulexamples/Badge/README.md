
This example is a simple model, that receives names of people and assigns a label either positive or negative to them.
It uses the second character of the first names as an input feature. This is a gold feature that can distinguish 100% between
the positive and negative class.
We show the usage of binary classifiers as well as multi-class classifiers in [here](BadgeClassifiers.scala).
In this file you could see we define two type of the classifiers that they take opposite labels. The goal is to simply show
how a simple constraint can impose the predictions of these two classifiers to be opposite.
The constrained versions of all these classifiers can be found [here](BadgeConstraintClassifiers.scala).
Using a simple constraint we train various joint models that train the two opposite classifiers jointly in [here](BadgesApp.scala).