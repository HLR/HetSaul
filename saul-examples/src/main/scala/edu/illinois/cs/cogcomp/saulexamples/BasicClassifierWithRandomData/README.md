This example made based on SVM-struct MATLAB test case. It is a replication of that example in Saul.
This is a very basic binary classifier with randomly generated data. The data is generated and then assigned a random label of "-1"
 or "1". Then the points are transformed in away that they are linearly separable. Therefore after training all the points are classified perfectly with
 100% accuracy.
 The dataModel is cached, for this example, such that the random data and properties are generated only once and in the first time that we call them;
 otherwise each new call will generate a new random value. 

