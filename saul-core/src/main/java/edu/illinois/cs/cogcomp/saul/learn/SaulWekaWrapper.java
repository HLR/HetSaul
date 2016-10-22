/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.learn;

import edu.illinois.cs.cogcomp.core.datastructures.vectors.ExceptionlessInputStream;
import edu.illinois.cs.cogcomp.core.datastructures.vectors.ExceptionlessOutputStream;
import edu.illinois.cs.cogcomp.lbjava.classify.*;
import edu.illinois.cs.cogcomp.lbjava.learn.Learner;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


/**
 * Translates Saul's internal problem representation into that which can be handled by WEKA
 * learning algorithms. This translation involves storing all lbjavaInstances in memory so they can be
 * passed to WEKA at one time.
 * <p>
 * <p>
 * WEKA must be available on your <code>CLASSPATH</code> in order to use this class. WEKA source
 * code and pre-compiled jar distributions are available at: <a
 * href="http://www.cs.waikato.ac.nz/ml/weka/">http://www.cs.waikato.ac.nz/ml/weka/</a>
 * <p>
 * <p>
 * To use this class in a Java application, the following restrictions must be recognized:
 * <ul>
 * <li> {@link #doneLearning()} must be called before calls to {@link #classify(Object)} can be made.
 * <li>After {@link #doneLearning()} is called, {@link #learn(Object)} may not be called without
 * first calling {@link #forget()}.
 * </ul>
 *
 * @author Taher Rahgooy
 **/

public class SaulWekaWrapper extends Learner {
    /**
     * Default for the {@link #baseClassifier} field.
     */
    public static final weka.classifiers.Classifier defaultBaseClassifier =
            new weka.classifiers.bayes.NaiveBayes();

    /**
     * Stores the instance of the WEKA classifier which we are training; default is
     * <code>weka.classifiers.bayes.NaiveBayes</code>.
     **/
    protected weka.classifiers.Classifier baseClassifier;
    /**
     * Stores a fresh instance of the WEKA classifier for the purposes of forgetting.
     **/
    protected weka.classifiers.Classifier freshClassifier;
    /**
     * Information about the features this learner takes as input stored here.
     **/
    protected FastVector attributeInfo;
    /**
     * The main collection of weka Instance objects.
     */
    protected Instances wekaInstances;
    /**
     * A buffer for lbjava instances.
     */
    protected List<LBJavaInstance> lbjavaInstances;
    /**
     * Indicates whether the {@link #doneLearning()} method has been called and the
     * {@link #forget()} method has not yet been called.
     **/
    protected boolean trained = false;
    /**
     * The label producing classifier's allowable values.
     */
    protected String[] allowableValues;


    /**
     * Empty constructor. Instantiates this wrapper with the default learning algorithm:
     * <code>weka.classifiers.bayes.NaiveBayes</code>. Attribute information must be provided before
     * any learning can occur.
     **/
    public SaulWekaWrapper() {
        this("");
    }

    /**
     * Partial constructor; attribute information must be provided before any learning can occur.
     *
     * @param base The classifier to be used in this system.
     **/
    public SaulWekaWrapper(weka.classifiers.Classifier base) {
        this("", base);
    }

    /**
     * Empty constructor. Instantiates this wrapper with the default learning algorithm:
     * <code>weka.classifiers.bayes.NaiveBayes</code>. Attribute information must be provided before
     * any learning can occur.
     *
     * @param n The name of the classifier.
     **/
    public SaulWekaWrapper(String n) {
        this(n, defaultBaseClassifier);
    }


    /**
     * Full Constructor.
     *
     * @param n    The name of the classifier
     * @param base The classifier to be used in this system.
     *             have.
     **/
    public SaulWekaWrapper(String n, weka.classifiers.Classifier base) {
        super(n);
        baseClassifier = base;
        freshClassifier = base;
        lbjavaInstances = new ArrayList<>();
    }


    /**
     * This learner's output type is <code>"mixed%"</code>.
     */
    public String getOutputType() {
        return "mixed%";
    }


    /**
     * Sets the labeler.
     *
     * @param l A labeling classifier.
     **/
    public void setLabeler(Classifier l) {
        super.setLabeler(l);
        allowableValues = l == null ? null : l.allowableValues();
    }


    /**
     * Returns the array of allowable values that a feature returned by this classifier may take.
     *
     * @return The allowable values of this learner's labeler, or an array of length zero if the
     * labeler has not yet been established or does not specify allowable values.
     **/
    public String[] allowableValues() {
        if (allowableValues == null)
            return new String[0];
        return allowableValues;
    }


    /**
     * Since WEKA classifiers cannot learn online, this method causes no actual learning to occur,
     * it simply creates an object from this example and adds it to a set of
     * lbjavaInstances from which the classifier will be built once {@link #doneLearning()} is called.
     **/
    public void learn(int[] exampleFeatures, double[] exampleValues, int[] exampleLabels,
                      double[] labelValues) {
        checkIfCanTrain();
        lbjavaInstances.add(new LBJavaInstance(exampleFeatures, exampleValues, exampleLabels, labelValues));
    }


    /**
     * This method makes one or more decisions about a single object, returning those decisions as
     * Features in a vector.
     *
     * @param exampleFeatures The example's array of feature indices.
     * @param exampleValues   The example's array of feature values.
     * @return A feature vector with a single feature containing the prediction for this example.
     **/
    public FeatureVector classify(int[] exampleFeatures, double[] exampleValues) {
        if (!trained) {
            System.err.println("WekaWrapper: Error - Cannot make a classification with an "
                    + "untrained classifier.");
            new Exception().printStackTrace();
            System.exit(1);
        }

        /*
         * Assuming that the first Attribute in our attributeInfo vector is the class attribute,
         * decide which case we are in
         */
        Attribute classAtt = (Attribute) attributeInfo.elementAt(0);

        if (classAtt.isNominal() || classAtt.isString()) {
            double[] dist = getDistribution(exampleFeatures, exampleValues);
            int best = 0;
            for (int i = 1; i < dist.length; ++i)
                if (dist[i] > dist[best])
                    best = i;

            Feature label = labelLexicon.lookupKey(best);
            if (label == null)
                return new FeatureVector();
            String value = label.getStringValue();

            return new FeatureVector(new DiscretePrimitiveStringFeature(containingPackage, name,
                    "", value, valueIndexOf(value), (short) allowableValues().length));
        } else if (classAtt.isNumeric()) {
            return new FeatureVector(new RealPrimitiveStringFeature(containingPackage, name, "",
                    getDistribution(exampleFeatures, exampleValues)[0]));
        } else {
            System.err.println("WekaWrapper: Error - illegal class type.");
            new Exception().printStackTrace();
            System.exit(1);
        }

        return new FeatureVector();
    }

    public String discreteValue(int[] f, double[] v) {
        return classify(f, v).discreteValueArray()[0];
    }

    public double realValue(int[] f, double[] v) {
        return classify(f, v).realValueArray()[0];
    }

    /**
     * Returns a discrete distribution of the classifier's prediction values.
     *
     * @param exampleFeatures The example's array of feature indices.
     * @param exampleValues   The example's array of feature values.
     **/
    protected double[] getDistribution(int[] exampleFeatures, double[] exampleValues) {
        if (!trained) {
            System.err.println("WekaWrapper: Error - Cannot make a classification with an "
                    + "untrained classifier.");
            new Exception().printStackTrace();
            System.exit(1);
        }

        Instance inQuestion =
                makeInstance(new LBJavaInstance(exampleFeatures, exampleValues, new int[0], new double[0]));

        /*
         * For Numerical class values, this will return an array of size 1, containing the class
         * prediction. For Nominal classes, an array of size equal to that of the class list,
         * representing probabilities. For String classes, ?
         */
        double[] dist = null;
        try {
            dist = baseClassifier.distributionForInstance(inQuestion);
        } catch (Exception e) {
            System.err.println("WekaWrapper: Error while computing distribution.");
            e.printStackTrace();
            System.exit(1);
        }

        if (dist.length == 0) {
            System.err.println("WekaWrapper: Error - The base classifier returned an empty "
                    + "probability distribution when attempting to classify an " + "example.");
            new Exception().printStackTrace();
            System.exit(1);
        }

        return dist;
    }


    /**
     * Destroys the learned version of the WEKA classifier and empties the {@link #wekaInstances}
     * collection of wekaInstances.
     **/
    public void forget() {
        super.forget();

        try {
            baseClassifier = weka.classifiers.Classifier.makeCopy(freshClassifier);
        } catch (Exception e) {
            System.err.println("LBJava ERROR: WekaWrapper.forget: Can't copy classifier:");
            e.printStackTrace();
            System.exit(1);
        }

        lbjavaInstances = new ArrayList<>();
        wekaInstances = new Instances(name, attributeInfo, 0);
        wekaInstances.setClassIndex(0);
        trained = false;
    }


    private void initializeAttributes() {
        attributeInfo = new FastVector(lexicon.size() + 1);
        /*
         * Here, we assume that if either the labels FeatureVector is empty of features, or is null,
         * then this example is to be considered unlabeled.
         */
        if (labelLexicon.size() < 1) {
            System.err.println("WekaWrapper: Error - Weka Instances may only take a single class "
                    + "value, ");
            new Exception().printStackTrace();
            System.exit(1);
        } else {
            Feature label = labelLexicon.lookupKey(0);
            if (!label.isDiscrete()) {
                Attribute a = new Attribute(label.getStringIdentifier());
                attributeInfo.addElement(a);
            } else {
                FastVector valueVector = new FastVector(labelLexicon.size());
                for (int v = 0; v < labelLexicon.size(); v++)
                    valueVector.addElement(labelLexicon.lookupKey(v).getStringValue());
                Attribute a = new Attribute(label.getGeneratingClassifier(), valueVector);
                attributeInfo.addElement(a);
            }
        }
        /*
         * Construct weka attribute for each lexicon entry.
         * If entry is discrete use a binary attribute
         * If it is real, use a numerical attribute
         */
        FastVector binaryValues = new FastVector(2);
        binaryValues.addElement("0");
        binaryValues.addElement("1");
        for (int featureIndex = 0; featureIndex < lexicon.size(); ++featureIndex) {
            Feature f = lexicon.lookupKey(featureIndex);
            Attribute a = f.isDiscrete() ?
                    new Attribute(f.toString(), binaryValues) :
                    new Attribute(f.toString());

            attributeInfo.addElement(a);
        }

        // The first attribute is the label
        wekaInstances = new Instances(name, attributeInfo, 0);
        wekaInstances.setClassIndex(0);
    }


    /**
     * Creates a WEKA Instance object out of a {@link FeatureVector}.
     **/
    private Instance makeInstance(LBJavaInstance instance) {

        // Initialize an Instance object
        Instance inst = new Instance(attributeInfo.size());

        // Acknowledge that this instance will be a member of our dataset 'wekaInstances'
        inst.setDataset(wekaInstances);

        // set all nominal feature values to 0, which means those features are not used in this example
        for(int i=1; i< attributeInfo.size();i++)
            if(inst.attribute(i).isNominal())
                inst.setValue(i, "0");

        // Assign values for its attributes
        /*
         * Since we are iterating through this example's feature list, which does not contain the
         * label feature (the label feature is the first in the 'attribute' list), we set attIndex
         * to at exampleFeatures[featureIndices] + 1, while we start featureIndices at 0.
         */
        for (int featureIndex = 0; featureIndex < instance.featureIndices.length; ++featureIndex) {
            int attIndex = instance.featureIndices[featureIndex] + 1;
            Feature f = lexicon.lookupKey(instance.featureIndices[featureIndex]);

            // if the feature does not exist, do nothing. this may occur in test set.
            if (f == null)
                continue;
            Attribute att = (Attribute) attributeInfo.elementAt(attIndex);

            // make sure the feature and the attribute match
            if (!(att.name().equals(f.toString()))) {
                System.err.println("WekaWrapper: Error - makeInstance encountered a misaligned "
                        + "attribute-feature pair.");
                System.err.println("  " + att.name() + " and " + f.toString()
                        + " should have been identical.");
                new Exception().printStackTrace();
                System.exit(1);
            }
            if (f.isDiscrete())
                inst.setValue(attIndex, "1"); // this feature is used in this example so we set it to "1"
            else
                inst.setValue(attIndex, instance.featureValues[featureIndex]);

        }

        /*
         * Here, we assume that if either the labels FeatureVector is empty of features, or is null,
         * then this example is to be considered unlabeled.
         */
        if (instance.labelIndices.length == 0) {
            inst.setClassMissing();
        } else if (instance.labelIndices.length > 1) {
            System.err.println("WekaWrapper: Error - Weka Instances may only take a single class "
                    + "value, ");
            new Exception().printStackTrace();
            System.exit(1);
        } else {
            Feature label = labelLexicon.lookupKey(instance.labelIndices[0]);

            // make sure the label feature matches the n 0'th attribute
            if (!(label.getGeneratingClassifier().equals(((Attribute) attributeInfo.elementAt(0))
                    .name()))) {
                System.err.println("WekaWrapper: Error - makeInstance found the wrong label name.");
                new Exception().printStackTrace();
                System.exit(1);
            }

            if (!label.isDiscrete())
                inst.setValue(0, instance.labelValues[0]);
            else
                inst.setValue(0, label.getStringValue());
        }

        return inst;
    }


    /**
     * Produces a set of scores indicating the degree to which each possible discrete classification
     * value is associated with the given example object.
     **/
    public ScoreSet scores(int[] exampleFeatures, double[] exampleValues) {
        double[] dist = getDistribution(exampleFeatures, exampleValues);

        /*
         * Assuming that the first Attribute in our attributeInfo vector is the class attribute,
         * decide which case we are in
         */
        Attribute classAtt = (Attribute) attributeInfo.elementAt(0);

        ScoreSet scores = new ScoreSet();

        if (classAtt.isNominal() || classAtt.isString()) {
            Enumeration enumeratedValues = classAtt.enumerateValues();

            int i = 0;
            while (enumeratedValues.hasMoreElements()) {
                if (i >= dist.length) {
                    System.err
                            .println("WekaWrapper: Error - scores found more possible values than "
                                    + "probabilities.");
                    new Exception().printStackTrace();
                    System.exit(1);
                }
                double s = dist[i];
                String v = (String) enumeratedValues.nextElement();
                scores.put(v, s);
                ++i;
            }
        } else if (classAtt.isNumeric()) {
            System.err.println("WekaWrapper: Error - The 'scores' function should not be called "
                    + "when the class attribute is numeric.");
            new Exception().printStackTrace();
            System.exit(1);
        } else {
            System.err.println("WekaWrapper: Error - ScoreSet: Class Types must be either "
                    + "Nominal, String, or Numeric.");
            new Exception().printStackTrace();
            System.exit(1);
        }

        return scores;
    }


    /**
     * Indicates that the classifier is finished learning. This method <I>must</I> be called if the
     * WEKA classifier is to learn anything. Since WEKA classifiers cannot learn online, all of the
     * training lbjavaInstances must be gathered and committed to first. This method invokes the WEKA
     * classifier's <code>buildClassifier(Instances)</code> method.
     **/
    public void doneLearning() {

        checkIfCanTrain();
        /*
         * System.out.println("\nWekaWrapper Data Summary:");
         * System.out.println(wekaInstances.toSummaryString());
         */

        try {
            initializeAttributes();
            for (LBJavaInstance i : lbjavaInstances)
                wekaInstances.add(makeInstance(i));
            lbjavaInstances.clear();

            baseClassifier.buildClassifier(wekaInstances);
        } catch (Exception e) {
            System.err.println("WekaWrapper: Error - There was a problem building the classifier");
            if (baseClassifier == null)
                System.out.println("WekaWrapper: baseClassifier was null.");
            e.printStackTrace();
            System.exit(1);
        }

        trained = true;
        wekaInstances = new Instances(name, attributeInfo, 0);
        wekaInstances.setClassIndex(0);
    }

    private void checkIfCanTrain() {
        if (trained) {
            System.err.println("WekaWrapper: Error - Cannot call 'doneLearning()' or 'learn()' again without "
                    + "first calling 'forget()'");
            new Exception().printStackTrace();
            System.exit(1);
        }
    }


    /**
     * Writes the settings of the classifier in use, and a string describing the classifier, if
     * available.
     **/
    public void write(PrintStream out) {
        out.print(name + ": ");
        String[] options = baseClassifier.getOptions();
        for (int i = 0; i < options.length; ++i)
            out.println(options[i]);
        out.println(baseClassifier);
    }


    /**
     * Writes the learned function's internal representation in binary form.
     *
     * @param out The output stream.
     **/
    public void write(ExceptionlessOutputStream out) {
        super.write(out);
        out.writeBoolean(trained);

        if (allowableValues == null)
            out.writeInt(0);
        else {
            out.writeInt(allowableValues.length);
            for (int i = 0; i < allowableValues.length; ++i)
                out.writeString(allowableValues[i]);
        }

        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(out);
        } catch (Exception e) {
            System.err.println("Can't create object stream for '" + name + "': " + e);
            System.exit(1);
        }

        try {
            oos.writeObject(baseClassifier);
            oos.writeObject(freshClassifier);
            oos.writeObject(attributeInfo);
            oos.writeObject(wekaInstances);
        } catch (Exception e) {
            System.err.println("Can't write to object stream for '" + name + "': " + e);
            System.exit(1);
        }
    }


    /**
     * Reads the binary representation of a learner with this object's run-time type, overwriting
     * any and all learned or manually specified parameters as well as the label lexicon but without
     * modifying the feature lexicon.
     *
     * @param in The input stream.
     **/
    public void read(ExceptionlessInputStream in) {
        super.read(in);
        trained = in.readBoolean();
        allowableValues = new String[in.readInt()];
        for (int i = 0; i < allowableValues.length; ++i)
            allowableValues[i] = in.readString();

        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(in);
        } catch (Exception e) {
            System.err.println("Can't create object stream for '" + name + "': " + e);
            System.exit(1);
        }

        try {
            baseClassifier = (weka.classifiers.Classifier) ois.readObject();
            freshClassifier = (weka.classifiers.Classifier) ois.readObject();
            attributeInfo = (FastVector) ois.readObject();
            wekaInstances = (Instances) ois.readObject();
        } catch (Exception e) {
            System.err.println("Can't read from object stream for '" + name + "': " + e);
            System.exit(1);
        }
    }

    private class LBJavaInstance{
        private final int[] featureIndices;
        private final double[] featureValues;
        private final int[] labelIndices;
        private final double[] labelValues;
        LBJavaInstance(int[] featureIndex, double[] featureValues, int[] labelIndex, double[] labelValues) {
            this.featureIndices = featureIndex;

            this.featureValues = featureValues;
            this.labelIndices = labelIndex;
            this.labelValues = labelValues;
        }
    }
}

