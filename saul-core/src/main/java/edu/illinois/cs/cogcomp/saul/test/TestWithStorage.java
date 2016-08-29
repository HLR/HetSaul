/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.test;

import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.lbjava.classify.Classifier;
import edu.illinois.cs.cogcomp.lbjava.classify.TestDiscrete;
import edu.illinois.cs.cogcomp.lbjava.parse.Parser;

import java.io.IOException;
import java.util.Date;

/**
 * A copy of {@link TestDiscrete} with the ability to store the prediction for each example.
 *
 * @author Christos Christodoulopoulos
 */
public class TestWithStorage {
    public static void test(TestDiscrete tester, Classifier classifier, Classifier oracle,
                            Parser parser, String outFile, int outputGranularity, String exclude) {
        int processed = 1;
        long totalTime = 0;
        if (!exclude.isEmpty()) {
            tester.addNull(exclude);
        }

        for (Object example = parser.next(); example != null; example = parser.next(), ++processed) {
            if (outputGranularity > 0 && processed % outputGranularity == 0)
                System.out.println(processed + " examples tested at " + new Date());

            totalTime -= System.currentTimeMillis();
            String prediction = classifier.discreteValue(example);
            totalTime += System.currentTimeMillis();
            assert prediction != null : "Classifier returned null prediction for example " + example;

            String gold = oracle.discreteValue(example);

            tester.reportPrediction(prediction, gold);

            // Append the predictions to a file (if the outFile parameter is given)
            if (outFile != null) {
                try {
                    String line = "Example " + processed + "\tprediction:\t" + prediction + "\t gold:\t" + gold;
                    line += "\t" + (gold.equals(prediction) ? "correct" : "incorrect");
                    LineIO.append(outFile, line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println(processed + " examples tested at " + new Date() + "\n");

        System.out.println("Average evaluation time: " + (totalTime / (1000.0 * processed)) + " seconds\n");
        tester.printPerformance(System.out);
    }
}
