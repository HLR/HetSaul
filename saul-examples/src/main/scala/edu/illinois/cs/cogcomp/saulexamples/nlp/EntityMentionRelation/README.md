# Entity-Mention-Relation classification 

This is the problem of recognizing the `kill (KFJ, Oswald)` relation in the sentence "J. V. 
Oswald was murdered at JFK after his assassin, R. U. KFJ..." This task requires making several
local decisions, such as identifying named entities in the sentence, in order to support the 
relation identification. For example, it may be useful to identify that Oswald and KFJ are 
people, and JFK is a location. This, in turn, may help to identify that the kill action is 
described in the sentence. At the same time, the relation kill constrains its arguments to 
be people (or at least, not to be locations) and helps to enforce that Oswald and KFJ are 
likely to be people, while JFK is not.

The problem is defined in terms of a collection of discrete random variables representing 
binary relations and their arguments; we seek an optimal assignment to the variables in 
the presence of the constraints on the binary relations between variables and the relation 
types.

## Training and inference paradigms

In this example we show how to model this problem and show different training/inference paradigms 
for this problem. 

 - Independent Training: In this scenario the entity and relation classifiers, are trained independently using only 
            local features. In particular, the relation classifier does not know the labels of its entity arguments,
            and the entity classifier does not know the labels of relations in the sentence either. 
            
 - Pipeline Training: Pipeline, mimics the typical strategy in solving complex natural language problems – separating a 
            task into several stages and solving them sequentially. For example, a named entity recognizer may be 
            trained using a different corpus in advance, and given to a relation classifier as a tool to extract 
            features. This approach first trains an entity classifier, and then uses the prediction of entities in 
            addition to other local features to learn the relation identifier. Note that although the true labels of 
            entities are known when training the relation identifier, this may not be the case in the testing time since 
            only the predicted entity labels are available in testing, learning on the predictions of the entity 
            classifier presumably makes the relation classifier more tolerant to the mistakes of the entity classifier. 
            In fact, we also observe this phenomenon empirically. When the relation classifier is trained using the 
            true entity labels, the performance is much worse than using the predicted entity labels.
 - L+I Training: 
 - Joint Training: 

[1] D. Roth and W-t Yih. "A Linear Programming Formulation for Global Inference in Natural Language Tasks." In Proceedings of CoNLL-2004. 2004.
[2] M. Chang, L. Ratinov, and D. Roth. "Structured learning with constrained conditional models." Machine Learning,
    88(3):399–431, 6 2012.