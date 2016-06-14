#1 Introduction
Saul is a modeling language implemented as a domain specific language (DSL) in Scala.
The main goal of Saul is to facilitate designing machine learning models with arbitrary configurations for the application programmer, including:

* Interacting with raw data and setting it in a flexible graph structure (i.e. data model) using the original available data structures.
* Relational feature extraction by flexible querying from the data model graph.
* Designing flexible learning models including various configurations such as:

   * Local models i.e. single classifiers. (Learning only models (LO)).
   * Constrained conditional models (CCM)[1] for training independent classifiers and using them jointly for global decision making in prediction time. (Learning+Inference (L+I)).
   * Global models for joint training and joint inference (Inference-Based-Training (IBT)).
   * Pipeline models for complex problems where the output of each layer is used as the input of the next layer.

The flexibility in designing above components helps rapid development of intelligent AI software systems with one or more learned functions that interact with each other.

Saul offers a convenient, declarative syntax for classifier and constraint definition directly in terms of the objects in the programmer's application.
With Saul, the details of feature extraction, learning, model evaluation, and inference are all abstracted away from the programmer, leaving him to reason more directly about his application.

###Further reading

[1] M. Chang and L. Ratinov and D. Roth, Structured Learning with Constrained Conditional Models Machine Learning (2012) pp.399-431.
 