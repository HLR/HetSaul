/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.test;

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier;
import edu.illinois.cs.cogcomp.lbjava.classify.TestDiscrete;
import edu.illinois.cs.cogcomp.lbjava.learn.Learner;
import edu.illinois.cs.cogcomp.lbjava.parse.Parser;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

import java.util.Arrays;

/**
 * Similar to {@link TestDiscrete} that works with real/continuous valued
 * outputs.
 *
 * @author Parisa
 * @since 6/28/15.
 */
public class TestReal {

    public TestReal(Learner classifier, Classifier oracle, Parser parser) {
        int examples = 0;
        double totalDifference = 0;
        double[] actuals = {};
        double[] predictions = {};

        classifier.write(System.out);

        for (Object example = parser.next(); example != null; example = parser.next()) {
            double prediction = classifier.realValue(example);

            predictions = Arrays.copyOf(predictions, predictions.length + 1);
            predictions[predictions.length - 1] = prediction;

            double value = oracle.realValue(example);

            actuals = Arrays.copyOf(actuals, actuals.length + 1);
            actuals[actuals.length - 1] = value;

            double difference = Math.abs(prediction - value);
            totalDifference += difference;
            classifier.classify(example);
            ++examples;

            System.out.println("Example " + examples + " difference: " + difference
                    + " (prediction: " + prediction + ")");
        }
        System.out.println("test examples number: " + examples);
        double avg = totalDifference / examples;
        System.out.println("Average difference: " + avg);
        double p = getPearsonCorrelation(predictions, actuals);
        System.out.println("Pearson correlation:" + p);
        SpearmansCorrelation e = new SpearmansCorrelation();
        double sp = e.correlation(predictions, actuals);
        System.out.println("Spearman correlation:" + sp);

    }

    public static double getPearsonCorrelation(double[] scores1, double[] scores2) {
        double result;
        double sum_sq_x = 0;
        double sum_sq_y = 0;
        double sum_coproduct = 0;
        double mean_x = scores1[0];
        double mean_y = scores2[0];

        for (int i = 2; i < scores1.length + 1; i += 1) {
            double sweep = (double) (i - 1) / i;
            double delta_x = scores1[i - 1] - mean_x;
            double delta_y = scores2[i - 1] - mean_y;
            sum_sq_x += delta_x * delta_x * sweep;
            sum_sq_y += delta_y * delta_y * sweep;
            sum_coproduct += delta_x * delta_y * sweep;
            mean_x += delta_x / i;
            mean_y += delta_y / i;
        }

        double pop_sd_x = Math.sqrt(sum_sq_x / scores1.length);
        double pop_sd_y = Math.sqrt(sum_sq_y / scores1.length);

        double cov_x_y = sum_coproduct / scores1.length;
        result = cov_x_y / (pop_sd_x * pop_sd_y);

        return result;
    }
}
