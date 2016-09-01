# Saul: Declarative Learning  Based Programming

Saul is a modeling language implemented as a domain specific language (DSL) in Scala.
The main goal of Saul is to facilitate designing machine learning models with arbitrary configurations for the application programmer, including:

* Interacting with raw data and setting it in a flexible graph structure (i.e. data model) using the original available data structures.
* Relational feature extraction by flexible querying from the data model graph.
* Designing flexible learning models including various configurations in which learners interact.

The flexibility in designing above components helps rapid development of intelligent AI systems with one or more learned functions that interact with each other.
Saul offers a convenient, declarative syntax for classifier and constraint definition directly in terms of the objects in the programmer's application.
With Saul, the details of feature extraction, learning, model evaluation, and inference are all abstracted away from the programmer, leaving him to reason more directly about his application.

# Tutorial
Visit each link for its content

 1. [Introduction](doc/INTRO.md)
 2. [Installation] (doc/INSTALLATION.md)
 3. [Conceptual structure of a Saul program](doc/CONCEPTUALSTRUCTURE.md)
 2. [Data modeling and feature extraction](doc/DATAMODELING.md)
 3. [Learners and constraints](doc/SAULLANGUAGE.md)
 4. [Model configurations](doc/Models.md)
 5. [Saul library](doc/LBJLIBRARY.md)


## Credits 
This project has been started by [Parisa Kordjamshidi](mailto:kordjam@illinois.edu) and the development has been done in collaboration with [Hao Wu](mailto:haowu4@illinois.edu), [Sameer Singh](mailto:sameer@cs.washington.edu), [Daniel Khashabi](mailto:khashab2@illinois.edu), [Christos Christodoulopoulos](mailto:christod@illinois.edu). 

If you use this tool, please cite the following paper: 

[1] Parisa Kordjamshidi, Dan Roth, and Hao Wu. "Saul: Towards declarative learning based programming." 
Proceedings of the International Joint Conferences on Artificial Intelligence (IJCAI), 2015.

## Licensing
_To see the full license for this software, see the LICENSE (in `doc` directory) or visit the download page 
for this software and press `Download`. The next screen displays the license._
