package edu.illinois.cs.cogcomp.saul.learn;

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
    private static final int SIGMOID = 1;
    private static final int TANH = 2;
    private static final int RELU = 3;
    private static final int LEAKY_RELU = 4;
    private static final int SOFTPLUS = 5;

    //Default learning parameters
    private static double default_learning_rate = 0.1d;
    private static double default_momentum = 0.1d;
    private static double default_lambda = 0.00001d;

    //Default network parameters. For sparse feature vectors, the network will automatically resize itself.
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
            for (double[] row : weights)
                Arrays.fill(row,0.0d);
            gradients = new double[num_inputs+1][num_outputs];
            for (double[] row : gradients)
                Arrays.fill(row,0.0d);
            prev_gradients = new double[num_inputs+1][num_outputs];
            for (double[] row : prev_gradients)
                Arrays.fill(row,0.0d);
            pre_activation = new double[num_outputs];
            Arrays.fill(pre_activation,0.0d);
            post_activation = new double[num_outputs];
            Arrays.fill(post_activation,0.0d);
            errors = new double[num_outputs];
            Arrays.fill(errors,0.0d);
            this.activation_function = activation_function;
        }
    }

    //Construct a perceptron with the default parameters defined above.
    public MultilayerPerceptron(){
        this(default_num_inputs,default_num_outputs,default_hidden_layers,default_activation_function);
    }

    //Construct a perceptron with user specified parameters.
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
     * Trains the neural network on a sparse input vector, using default learning parameters. Use for binary
     * classification only.
     *
     * @param exampleFeatures The example's array of feature indices.
     * @param exampleValues The example's array of feature values.
     * @param exampleLabels The example's label(s).
     * @param labelValues The labels' values.
     */
    public void learn(int[] exampleFeatures, double[] exampleValues, int[] exampleLabels, double[] labelValues){
        if (((++iterations)%10000) == 0) {
            System.out.println("learning: " + iterations);
        }
        assert exampleLabels.length == 1 : "Example must have a single label.";
        double[] output;
        if (exampleLabels[0] == 0){
            output = new double[]{1.0d,0.0d};
        }else{
            output = new double[]{0.0d,1.0d};
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
        for (int i = 0; i < layers[0].errors.length; i++){ //for each output node
            layers[0].gradients[0][i] = -layers[0].errors[i];
            for (int j = 0; j < exampleFeatures.length; j++){
                if (exampleFeatures[j] < layers[0].weights.length-1)
                    layers[0].gradients[exampleFeatures[j]+1][i] = layers[0].errors[i]*(layers[0].post_activation[i]);
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
    public void resize_inputs(int target) {
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

    //for binary classification, computes the output feature label ("positive" or "negative") given a sparse input vector
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
        if (best_index == 0){ //for binary classifiers used in Twitter sentiment analysis
            return new DiscretePrimitiveStringFeature(containingPackage, name, "",
                    "negative", (short)0, (short)0);
        }
        return new DiscretePrimitiveStringFeature(containingPackage, name, "",
                "positive", (short)0, (short)0);
    }

    //compute activation given an input feature vector
    public double[] infer(double[] input){
        compute_activation(input);
        return layers[layers.length-1].post_activation;
    }

    //compute activation given a sparse input feature vector
    public double[] infer (int[] exampleFeatures, double[] exampleValues){
        compute_activation(exampleFeatures,exampleValues);
        double[] result = new double[layers[layers.length-1].errors.length];
        for (int i = 0; i < result.length; i++){
            result[i] = layers[layers.length-1].post_activation[i];
        }
        return result;
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

    public ScoreSet scores(Object example){
        return null;
    }

    public ScoreSet scores(int[] exampleFeatures, double[] exampleValues) {
        return null;
    }

    public void write(ExceptionlessOutputStream out) {
        out.close();
    }

    public void write(PrintStream out) {
        out.close();
    }

    public FeatureVector classify(int[] exampleFeatures, double[] exampleValues) {
        double result = infer(exampleFeatures,exampleValues)[0];
        return new FeatureVector(new RealPrimitiveStringFeature(containingPackage, name,"",result));
    }

    public String discreteValue(int[] exampleFeatures, double[] exampleValues){
        /*if (((++iterations2)%100) == 0) {
            System.out.println("learning: " + iterations2);
        }*/
        return featureValue(exampleFeatures,exampleValues).getStringValue();
    }

}