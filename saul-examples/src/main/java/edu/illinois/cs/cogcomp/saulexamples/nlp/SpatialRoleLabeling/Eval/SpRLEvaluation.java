package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Eval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Taher on 2016-09-20.
 */
public class SpRLEvaluation {
    private final String label;
    private final double precision;
    private final double recall;
    private final double f1;
    private final int labeledCount;
    private final int predictedCount;
    private final List<SpRLEval> fp;
    private final List<SpRLEval> fn;
    private final Map<SpRLEval, SpRLEval> tp;

    public SpRLEvaluation(String label, double precision, double recall, double f1, int labeledCount, int predictedCount) {
        this.label = label;
        this.precision = precision;
        this.recall = recall;
        this.f1 = f1;
        this.labeledCount = labeledCount;
        this.predictedCount = predictedCount;
        fp = new ArrayList<>();
        fn = new ArrayList<>();
        tp = new HashMap<>();
    }

    public String getLabel() {
        return label;
    }

    public double getPrecision() {
        return precision;
    }

    public double getRecall() {
        return recall;
    }

    public int getLabeledCount() {
        return labeledCount;
    }

    public int getPredictedCount() {
        return predictedCount;
    }

    public double getF1() {
        return f1;
    }

    public List<SpRLEval> getFp() {
        return fp;
    }

    public List<SpRLEval> getFn() {
        return fn;
    }

    public Map<SpRLEval, SpRLEval> getTp() {
        return tp;
    }
}
