package edu.illinois.cs.cogcomp.saul.learn;

import edu.illinois.cs.cogcomp.core.datastructures.vectors.ExceptionlessInputStream;
import edu.illinois.cs.cogcomp.core.datastructures.vectors.ExceptionlessOutputStream;
import edu.illinois.cs.cogcomp.lbjava.classify.*;
import edu.illinois.cs.cogcomp.lbjava.learn.Learner;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;

/**
 * Use the <code>MultilayerPerceptron</code> to train and subsequently test a neural network for regression or classification.
 *
 * <p>One can specify the number of hidden layers as well as the size of each layer and the activation function to use for each layer.
 * For classification tasks, it is strongly recommended to use a sigmoid activation at the final layer. This implementation works
 * for both sparse and dense feature vectors. Use the appropriate functions based on the representation of your feature vector.
 * </p>
 *
 * @author      Abiyaz Chowdhury <chowdh2@cooper.edu>
 */

final public class MultilayerPerceptron extends Learner{
    /**
     * Activation function codes. When calling the {@link MultilayerPerceptron} constructor to specify activation functions, use these values to specify
     * the activation functions.
     */
    private static final int SIGMOID = 1;
    private static final int TANH = 2;
    private static final int RELU = 3;
    private static final int LEAKY_RELU = 4;
    private static final int SOFTPLUS = 5;
    private static final int IDENTITY = 6;

    /**
     * Default neural network learning parameters.
     */
    private static double default_learning_rate = 0.1d;
    private static double default_momentum = 0.1d;
    private static double default_lambda = 0.00001d;

    /**
     * Default parameters for network initialization. For classification tasks, each node of the output layer corresponds
     * to a class. The default setting below is for binary classification.
     */
    private static int default_num_inputs = 128;
    private static int[] default_hidden_layers =  new int[]{};
    private static int[] default_activation_function = {SIGMOID};
    private static int default_num_outputs = 2;

    private static int iterations = 0;
    private static int iterations2 = 0;

    public Layer[] layers;

    /** Each layer contains a set of weights, gradients, and activation values. The aggregate of all layers comprise
     * the entire neural network.
     */
    public class Layer {
        public double[][] weights;
        private double[][] gradients;
        private double[][] prev_gradients;
        private double[] pre_activation;
        private double[] post_activation;
        private double[] errors;
        private int activation_function;

        // Initializes a layer of the neural network. Weights are all initialized to 0.
        private Layer(int num_inputs, int num_outputs, int activation_function){
            weights = new double[num_inputs+1][num_outputs];
            gradients = new double[num_inputs+1][num_outputs];
            prev_gradients = new double[num_inputs+1][num_outputs];
            pre_activation = new double[num_outputs];
            post_activation = new double[num_outputs];
            errors = new double[num_outputs];
            this.activation_function = activation_function;
        }

        public void write(ExceptionlessOutputStream out){
            out.writeInt(weights.length);
            out.writeInt(weights[0].length);
            for (int i = 0; i < weights.length; i++) {
                for (int j = 0; j < weights[i].length; j++){
                    out.writeDouble(weights[i][j]);
                }
            }
            out.writeInt(pre_activation.length);
            out.writeInt(activation_function);
        }

        public void write(PrintStream out){
            out.println(weights.length);
            out.println(weights[0].length);
            for (int i = 0; i < weights.length; i++) {
                for (int j = 0; j < weights[i].length; j++){
                    out.println(weights[i][j]);
                }
            }
            out.println(pre_activation.length);
            out.println(activation_function);
        }

        public void read(ExceptionlessInputStream in){
            int num_rows = in.readInt();
            int num_elements = in.readInt();
            weights = new double[num_rows][num_elements];
            gradients = new double[num_rows][num_elements];
            prev_gradients = new double[num_rows][num_elements];
            for (int i = 0; i < num_rows; i++){
                for (int j = 0; j < num_elements; j++){
                    weights[i][j] = in.readDouble();
                }
            }
            num_elements = in.readInt();
            pre_activation = new double[num_elements];
            post_activation = new double[num_elements];
            errors = new double[num_elements];
            activation_function = in.readInt();
        }
    }


    /**
     * Construct a perceptron with the default parameters defined above.
     */
    public MultilayerPerceptron(){
        this(default_num_inputs,default_num_outputs,default_hidden_layers,default_activation_function);
    }

    /**
     * Construct a perceptron with user specified parameters.
     * @param num_inputs Number of dimensions for (input) feature vector
     * @param num_outputs Number of nodes at output layer. For classification tasks, use one output node for each
     * possible class label.
     * @param hidden_layers Specify hidden layers. For example, {5,6,5} describes a neural network with three
     * fully connected hidden layers having sizes of 5,6,5 respectively.
     * @param activation_function Specify the activation functions for the layers, based on the codes for each function.
     * Number of entries in this array must be equal to 1 plus the number of hidden layers. The last entry corresponds
     * to activation of the output layer, which should always be sigmoid. For example, a network
     * with one hidden layer with a tanh at hidden layer and sigmoid at output layer can be specified with {2,1}.
     */
    public MultilayerPerceptron(int num_inputs, int num_outputs, int[] hidden_layers, int [] activation_function){
        super("");
        int[] layer_size = new int[hidden_layers.length+2];
        layer_size[0] = num_inputs;
        for (int i = 0; i < hidden_layers.length; i++){
            layer_size[i+1] = hidden_layers[i];
        }
        layer_size[hidden_layers.length+1] = num_outputs;

        layers = new Layer[hidden_layers.length+1];
        for (int i = 0; i < layers.length; i++){
            layers[i] = new Layer(layer_size[i],layer_size[i+1],activation_function[i]);
        }
    }

    /**
    * Trains the neural network on a given example, given a real input and output vector, using default learning parameters.
    *
    * @param input      Real vector consisting of the input features of an example
    * @param output     Real vector consisting of the labelled outputs of the neural network.
    */
    public void learn(double[] input, double[] output){
        learn(input,output,default_learning_rate,default_lambda,default_momentum);
    }

    /**
     * Trains the neural network on a given example, given a real input and output vector, using given learning parameters.
     *
     * @param input         Real vector consisting of the input features of an example
     * @param output        Real vector consisting of the labelled outputs of the neural network.
     * @param learning_rate Learning rate
     * @param lambda        Penalization constant for L-2 regularization
     * @param momentum      Momentum value for speeding convergence
     */
    public void learn(double[] input, double[] output, double learning_rate, double lambda, double momentum){
        reset_errors();
        compute_activation(input);
        output_errors(output);
        compute_gradients(input);
        update_weights(learning_rate,lambda, momentum);
    }

    /**
     * Trains the neural network on a sparse input vector, using default learning parameters.
     * @TODO Currenty there is no robust way to determine whether the user is intending to do classification or regression.
     * If there is a single output label, classification is performed. Otherwise regression is performed.
     * @param exampleFeatures The example's array of feature indices.
     * @param exampleValues The example's array of feature values.
     * @param exampleLabels The example's label(s). For classification tasks, this array should have a single element
     * equal to 0,1,2,...n-1 where n is the number of possible classes.
     * @param labelValues The labels' values.
     */
    public void learn(int[] exampleFeatures, double[] exampleValues, int[] exampleLabels, double[] labelValues){
        if (((++iterations)%10000) == 0) {
            System.out.println("learning: " + iterations);
        }
        double[] output;
        if (exampleLabels.length == 1) {  //CLASSIFICATION
            output = new double[layers[layers.length - 1].errors.length];
            output[exampleLabels[0]] = 1.0d;
        }else{  //REGRESSION
            if (exampleLabels.length != layers[layers.length - 1].errors.length){
                resize_outputs(exampleLabels.length);
            }
            layers[layers.length-1].activation_function = IDENTITY;
            output = labelValues;
        }
        int maximum_index = -1;
        for (int i = 0; i < exampleFeatures.length; i++){
            if  (exampleFeatures[i] > maximum_index){
                maximum_index = exampleFeatures[i];
            }
        }
        if (layers[0].weights.length < maximum_index + 2 )
            resize_inputs(maximum_index + 10000);
        reset_errors();
        compute_activation(exampleFeatures,exampleValues);
        output_errors(output);
        compute_gradients(exampleFeatures,exampleValues);
        update_weights(default_learning_rate,default_lambda,default_momentum);
    }

    //set error values in all layers to 0
    private void reset_errors(){ //set all errors of the entire network to zero to prepare for training
        for (int i = 0; i < layers.length; i++){
            Arrays.fill(layers[i].errors, 0.0d);
        }
    }

    //compute activations for the entire network given the input feature vector
    private void compute_activation(double[] input){
        //compute the activations for every layer
        for (int i = 0; i < layers.length; i++){ //for each layer
            for (int j = 0; j < layers[i].pre_activation.length; j++){ //for each output node in the given layer
                layers[i].pre_activation[j] = -layers[i].weights[0][j]; //set the pre-activation sum to bias
                for (int k = 1; k < ((i == 0) ? input.length : layers[i-1].errors.length)+ 1; k++){ //for each input node in the given layer
                    layers[i].pre_activation[j] += layers[i].weights[k][j] * (( i == 0) ? input[k - 1] : layers[i - 1].post_activation[k - 1]);
                }
                layers[i].post_activation[j] = apply_activation_function(layers[i].pre_activation[j],layers[i].activation_function);
            }
        }
    }

    //compute activations for the entire network given the sparse input feature vector
    private void compute_activation(int[] exampleFeatures, double[] exampleValues){
        //compute the activations for every layer
        for (int i = 0; i < layers.length; i++){ //for each layer
            for (int j = 0; j < layers[i].pre_activation.length; j++){ //for each output node in the given layer
                layers[i].pre_activation[j] = -layers[i].weights[0][j]; //set the pre-activation sum to bias
                if (i > 0){
                    for (int k = 1; k < layers[i-1].errors.length+ 1; k++){ //for each input node in the given layer
                        layers[i].pre_activation[j] += layers[i].weights[k][j] * (layers[i - 1].post_activation[k - 1]);
                    }
                }else{
                    for (int k = 0; k < exampleFeatures.length; k++){ //for each input node in the given layer
                        if (exampleFeatures[k] < layers[i].weights.length-1)
                            layers[i].pre_activation[j] += layers[i].weights[exampleFeatures[k]+1][j] * exampleValues[k];
                    }
                }
                layers[i].post_activation[j] = apply_activation_function(layers[i].pre_activation[j],layers[i].activation_function);
            }
        }
    }

    //compute error at the output layer, given the output label
    private void output_errors(double[] output){
        //compute error at output layer
        for (int i = 0; i < layers[layers.length-1].errors.length; i++)
            layers[layers.length - 1].errors[i] = (output[i] - layers[layers.length - 1].post_activation[i])*apply_activation_derivative(layers[layers.length-1].pre_activation[i],layers[layers.length-1].activation_function);
    }

    //compute gradients for every layer given real input feature vector
    private void compute_gradients(double[] input){
        for (int i = layers.length-1; i >= 0; i--){ //for each layer
            for (int j = 0; j < layers[i].errors.length; j++){ //for each output node
                layers[i].gradients[0][j] = -layers[i].errors[j];
                for (int k = 1; k < ((i == 0)? input.length : layers[i-1].errors.length)+1; k++){
                    layers[i].gradients[k][j] = layers[i].errors[j]*((i==0)? input[k-1]:layers[i-1].post_activation[k-1]);
                }
            }
            if (i > 0){
                for (int j = 0; j < layers[i-1].errors.length; j++){
                    for (int k = 0; k < layers[i].errors.length; k++) {
                        layers[i - 1].errors[j] += layers[i].weights[j + 1][k] * layers[i].errors[k];
                    }
                    layers[i-1].errors[j] *= apply_activation_derivative(layers[i-1].pre_activation[j],layers[i-1].activation_function);
                }
            }
        }
    }

    //compute gradients for every layer given sparse input feature vector
    private void compute_gradients(int[] exampleFeatures, double[] exampleValues){
        for (int i = layers.length-1; i > 0; i--){ //for each layer
            for (int j = 0; j < layers[i].errors.length; j++){ //for each output node
                layers[i].gradients[0][j] = -layers[i].errors[j];
                for (int k = 1; k < layers[i-1].errors.length+1; k++){
                    layers[i].gradients[k][j] = layers[i].errors[j]*(layers[i-1].post_activation[k-1]);
                }
            }
            for (int j = 0; j < layers[i-1].errors.length; j++) {
                for (int k = 0; k < layers[i].errors.length; k++) {
                    layers[i - 1].errors[j] += layers[i].weights[j + 1][k] * layers[i].errors[k];
                }
                layers[i - 1].errors[j] *= apply_activation_derivative(layers[i - 1].pre_activation[j], layers[i - 1].activation_function);
            }
        }
        for (int i = 0; i < layers[0].weights.length; i++){
            for (int j = 0; j < layers[0].weights[i].length; j++) {
                layers[0].gradients[i][j] = 0;
            }
        }
        for (int i = 0; i < layers[0].errors.length; i++){ //for each output node
            layers[0].gradients[0][i] = -layers[0].errors[i];
            for (int j = 0; j < exampleFeatures.length; j++){
                if (exampleFeatures[j] < layers[0].weights.length-1)
                    layers[0].gradients[exampleFeatures[j]+1][i] = layers[0].errors[i]*(exampleValues[j]);
            }
        }
    }

    //update weights for every layer given the computed gradients
    private void update_weights(double learning_rate,double lambda,double momentum ){
        for (int i = 0; i < layers.length; i++){ //update weights using computed gradients
            for (int j = 0; j < layers[i].weights.length; j++){
                for (int k = 0; k < layers[i].weights[j].length; k++) {
                    layers[i].weights[j][k] += learning_rate*(layers[i].gradients[j][k]-lambda*layers[i].weights[j][k])+momentum*layers[i].prev_gradients[j][k];
                    layers[i].prev_gradients[j][k] =  layers[i].gradients[j][k];
                }
            }
        }
    }

    //resize the first layer to accommodate more input features
    private void resize_inputs(int target) {
        if (layers[0].weights.length >= target + 2 ) {
            return;
        }
        layers[0].gradients = new double[target + 2][layers[0].errors.length];

        double[][] temp = new double[target + 2][layers[0].errors.length];
        for (double[] row : temp)
            Arrays.fill(row, 0.0d);
        for (int i = 0; i < layers[0].prev_gradients.length; i++) {
            temp[i] = Arrays.copyOf(layers[0].prev_gradients[i], layers[0].prev_gradients[i].length);
        }
        layers[0].prev_gradients = new double[target + 2][layers[0].errors.length];
        for (int i = 0; i < layers[0].prev_gradients.length; i++) {
            layers[0].prev_gradients[i] = Arrays.copyOf(temp[i], layers[0].prev_gradients[i].length);
        }
        Random r = new Random();
        for (double[] row : temp)
            Arrays.fill(row, 0.0d);
        for (int i = 0; i < layers[0].weights.length; i++ ){
            temp[i] = Arrays.copyOf(layers[0].weights[i], layers[0].weights[i].length);
        }
        layers[0].weights = new double[target+2][layers[0].errors.length];
        for (int i = 0; i < layers[0].weights.length; i++ ){
            layers[0].weights[i] = Arrays.copyOf(temp[i], layers[0].weights[i].length);
        }
        return;
    }

    //resize the output layer to accommodate a given number of output nodes
    private void resize_outputs(int target){
        int x = layers.length-1;
        layers[x] = new Layer(layers[x].weights.length-1,layers[x].errors.length,layers[x].activation_function);
    }


    /**
     * Returns the output layer given an input test feature vector. Use for testing.
     *
     * @param input The real input feature vector being tested.
     * @return The output layer, after the activation function has been applied.
     */
    public double[] infer(double[] input){
        compute_activation(input);
        return layers[layers.length-1].post_activation;
    }

    /**
     * Returns the output layer given a sparse input test feature vector. Use for testing.
     *
     * @param exampleFeatures The example's array of feature indices.
     * @param exampleValues The example's array of feature values.
     * @return The output layer, after the activation function has been applied.
     */
    public double[] infer (int[] exampleFeatures, double[] exampleValues){
        compute_activation(exampleFeatures,exampleValues);
        return layers[layers.length-1].post_activation;
    }

    //apply the activation function, given the argument and the type of activation function to be used
    private double apply_activation_function(double x, int activation_function){
        switch (activation_function){
            case SIGMOID:
                return sigmoid(x);
            case TANH:
                return tanh(x);
            case RELU:
                return relu(x);
            case LEAKY_RELU:
                return leaky_relu(x);
            case SOFTPLUS:
                return softplus(x);
            case IDENTITY:
                return x;
            default:
                System.out.println("Invalid activation function");
                return 0;
        }
    }

    //apply the derivative of the activation function, given the argument and the type of activation function to be used
    private double apply_activation_derivative(double x, int activation_function){
        switch (activation_function){
            case SIGMOID:
                return sigmoid_derivative(x);
            case TANH:
                return tanh_derivative(x);
            case RELU:
                return relu_derivative(x);
            case LEAKY_RELU:
                return leaky_relu_derivative(x);
            case SOFTPLUS:
                return softplus_derivative(x);
            case IDENTITY:
                return 1;
            default:
                System.out.println("Invalid activation function");
                return 0;
        }
    }

    //logistic function
    private double sigmoid(double x) {
        return(1.0d / (1.0d + Math.exp(-x)));
    }

    //logistic function derivative
    private double sigmoid_derivative(double x){
        return (sigmoid(x)*(1.0d - sigmoid(x)));
    }

    //hyperbolic tangent
    private double tanh(double x) {
        return((1.0d - Math.exp(-2.0d*x))/(1.0d + Math.exp(-2.0d*x)));
    }

    //hyperbolic tangent derivative
    private double tanh_derivative(double x) {
        return (1.0d - Math.pow(tanh(x),2.0d));
    }

    //rectified linear unit
    private double relu(double x) {
        return (Math.max(x,0.0d));
    }

    //rectified linear unit derivative
    private double relu_derivative(double x) {
        if (x > 0) return 1.0f;
        else if (x == 0) return 0;
        else return 0;
    }

    //leaky rectified linear unit
    private double leaky_relu(double x) {
        if (x > 0) return x;
        else return (0.01d*x);
    }

    //leaky rectified linear unit derivative
    private double leaky_relu_derivative(double x) {
        if (x > 0) return 1.0d;
        else if (x == 0) return 0;
        else return 0.01d;
    }

    //softplus function
    private double softplus(double x) {
        return  Math.log(1.0d + Math.exp(x));
    }

    //softplus function derivative
    private double softplus_derivative(double x) {
        return sigmoid(x);
    }

    //serializing
    public void write(ExceptionlessOutputStream out) {
        super.write(out);
        out.writeInt(layers.length);
        for (int i = 0; i < layers.length; i++) {
            layers[i].write(out);
        }
    }

    public void write(PrintStream out) {
        out.println(layers.length);
        for (int i = 0; i < layers.length; i++) {
            layers[i].write(out);
        }
        out.println("End of MultilayerPerceptron");
    }

    //deserializing
    public void read(ExceptionlessInputStream in) {
        super.read(in);
        int num_layers = in.readInt();
        for (int i = 0; i < num_layers; i++) {
            layers[i].read(in);
        }
    }

    /**
     * Returns the FeatureVector specifying the predicted label given an input.
     *
     * @param exampleFeatures The example's array of feature indices.
     * @param exampleValues The example's array of feature values.
     * @return FeatureVector object representing the output label given the input
     */
    public FeatureVector classify(int[] exampleFeatures, double[] exampleValues) {
        return new FeatureVector(featureValue(exampleFeatures, exampleValues));
    }

    public String discreteValue(int[] exampleFeatures, double[] exampleValues){
        return featureValue(exampleFeatures,exampleValues).getStringValue();
    }

    /**
     * Simply computes the dot product of the weight vector and the example
     *
     * @param exampleFeatures The example's array of feature indices.
     * @param exampleValues The example's array of feature values.
     * @return The computed activation of the first output neuron.
     **/
    public double realValue(int[] exampleFeatures, double[] exampleValues) {
        return infer(exampleFeatures,exampleValues)[0];
    }

    //for binary classification, computes the output feature label given a sparse input vector
    public Feature featureValue(int[] f, double[] v) {
        compute_activation(f,v);
        int best_index = -1;
        double best_value = -1;
        for (int i = 0; i < layers[layers.length-1].errors.length; i++){
            if (layers[layers.length-1].post_activation[i] > best_value){
                best_value = layers[layers.length-1].post_activation[i];
                best_index = i;
            }
        }
        best_index = Math.max(best_index,0);
        return new DiscretePrimitiveStringFeature(containingPackage, name, "", labelLexicon.lookupKey(best_index).getStringValue() , (short)0, (short)0);
    }

    public ScoreSet scores(Object example){
        Object[] exampleArray = getExampleArray(example, false);
        return scores((int[]) exampleArray[0], (double[]) exampleArray[1]);
    }

    /**
     * Returns the ScoreSet specifying confidence for each output class label.
     *
     * @param exampleFeatures The example's array of feature indices.
     * @param exampleValues The example's array of feature values.
     * @return ScoreSet object consisting of each output label and the corresponding score.
     */
    public ScoreSet scores(int[] exampleFeatures, double[] exampleValues) {
        ScoreSet result = new ScoreSet();
        double[] out = infer(exampleFeatures,exampleValues);
        for (int i = 0; i < out.length; i++) {
            result.put(labelLexicon.lookupKey(i).getStringValue(),out[i]);
        }
        return result;
    }

    //diagnostic, use for testing purposes only
    /*
    public void diagnostic(){
        double threshold = 0.2;
        for (int i = 0; i < layers[0].weights.length - 1; i++){
            if (layers[0].weights[i+1][0] > threshold){
                System.out.println("Highest valence: " + lexicon.lookupKey(i).getStringValue() + " score: " + layers[0].weights[i+1][0]);
            }else if (layers[0].weights[i+1][0] < -threshold){
                System.out.println("Lowest valence: " + lexicon.lookupKey(i).getStringValue() + " score: " + layers[0].weights[i+1][0]);
            }
            if (layers[0].weights[i+1][1] > threshold){
                System.out.println("Highest arousal: " + lexicon.lookupKey(i).getStringValue() + " score: " + layers[0].weights[i+1][1]);
            }else if (layers[0].weights[i+1][1] < -threshold){
                System.out.println("Lowest arousal: " + lexicon.lookupKey(i).getStringValue() + " score: " + layers[0].weights[i+1][1]);
            }
        }
    }*/
}