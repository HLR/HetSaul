package edu.illinois.cs.cogcomp.lfs;

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier;
import edu.illinois.cs.cogcomp.lbjava.frontend.parser;
import edu.illinois.cs.cogcomp.lbjava.learn.Learner;
import edu.illinois.cs.cogcomp.lbjava.parse.Parser;
import edu.illinois.cs.cogcomp.lfs.parser.LBJIteratorParserScala;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

import java.util.Arrays;

/**
 * Created by Parisa on 6/28/15.
 */
public class TestContinuos {
   double[] predictions={};
    double[] actuals={};
    public TestContinuos( Learner classifierx,
                          Classifier oracle,
                          Parser parser,
                          boolean output){



        int correct = 0, examples = 0;

        classifierx.write(System.out);
    double totalDifference = 0;
    for (Object example = parser.next(); example != null;

    example = parser.next())
    {
        double prediction = classifierx.realValue(example);

        predictions  = Arrays.copyOf(predictions, predictions.length + 1);
        predictions[predictions.length - 1] = prediction;

        double value = oracle.realValue(example);

        actuals=Arrays.copyOf(actuals,actuals.length+1);
        actuals[actuals.length-1]=value;

        double difference = Math.abs(prediction-value);
        totalDifference += difference;
        classifierx.classify(example);
        ++examples;

        System.out.println("Example " + examples + " difference: " + difference
                + " (prediction: " + prediction + ")");
    }
        System.out.println("test examples number: "+ examples);
        double avg = totalDifference / examples;
    System.out.println("Average difference: " + avg);
        double p=getPearsonCorrelation(predictions,actuals);
        System.out.println("Pearson correlation:" + p);
        SpearmansCorrelation e=new SpearmansCorrelation();
        double sp=e.correlation(predictions,actuals);
        System.out.println("Spearman correlation:" + sp);

    }

     public static double getPearsonCorrelation(double[] scores1,double[] scores2){

            double result = 0;

            double sum_sq_x = 0;

            double sum_sq_y = 0;

            double sum_coproduct = 0;

            double mean_x = scores1[0];

            double mean_y = scores2[0];

            for(int i=2;i<scores1.length+1;i+=1){

                double sweep =Double.valueOf(i-1)/i;

                double delta_x = scores1[i-1]-mean_x;

                double delta_y = scores2[i-1]-mean_y;

                sum_sq_x += delta_x * delta_x * sweep;

                sum_sq_y += delta_y * delta_y * sweep;

                sum_coproduct += delta_x * delta_y * sweep;

                mean_x += delta_x / i;

                mean_y += delta_y / i;

            }

            double pop_sd_x = (double) Math.sqrt(sum_sq_x/scores1.length);

            double pop_sd_y = (double) Math.sqrt(sum_sq_y/scores1.length);

            double cov_x_y = sum_coproduct / scores1.length;

            result = cov_x_y / (pop_sd_x*pop_sd_y);

            return result;

    }
}
