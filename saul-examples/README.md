# Saul Examples 

## Application examples

In this package two application examples in Saul are described. 
The list of the examples is listed bellow. To see more details on each example, click on its link to 
visit its README file. 

1. [Set Cover](src/main/scala/edu/illinois/cs/cogcomp/saulexamples/setcover/README.md): 
The Set Cover problem which is a classical constraint programming 
problem. This example shows declarative first order constraint programming in Saul. The constraints 
are propositionalized and form an integer linear program (ILP) which are solved using Gurobi as our backend solver.
 Note that there is no training/learning involved in this example. 
 
2. [Entity-Relation Extraction](src/main/scala/edu/illinois/cs/cogcomp/saulexamples/nlp/EntityRelation/README.md): 
The entity-relation extraction task through 
which designing various training and prediction configurations are exemplified. 
One can see how local, global and pipeline configurations are designed, used and evaluated in Saul.

3. [Spam Classification](src/main/scala/edu/illinois/cs/cogcomp/saulexamples/nlp/EmailSpam/README.md): 
A third example which is a binary classification task 
to classify text documents as either Spam or Not Spam was also created.

4. [Semantic Role Labeling](src/main/scala/edu/illinois/cs/cogcomp/saulexamples/nlp/SemanticRoleLabeling/README.md): 
a task in natural language processing consisting of the detection of the semantic arguments associated with the predicate 
or verb of a sentence and their classification into their specific roles.

5. [Spatial Role Labeling](src/main/scala/edu/illinois/cs/cogcomp/saulexamples/nlp/SpatialRoleLabeling/README.md): 
In Spatial Role Labeling, we try to find spatial relations and label spatial roles such as trajectors, landmarks, and spatial indicators in the sentence. 

6. [Part-of-Speech Tagging](src/main/scala/edu/illinois/cs/cogcomp/saulexamples/nlp/POSTagger/README.md): 
Part-of-Speech Tagging is the identification of words as nouns, verbs, adjectives, adverbs, etc. 

* Note: Examples are under active development. 

## Prerequisites for this examples package 

* JDK 1.7 or Higher
* Saul 
* Gurobi (required for constrained inference)
