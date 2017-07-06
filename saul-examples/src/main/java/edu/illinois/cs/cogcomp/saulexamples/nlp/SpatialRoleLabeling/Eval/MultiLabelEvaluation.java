package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Eval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by taher on 2017-04-18.
 */
public class MultiLabelEvaluation {
    private final Map<String, Integer> tp;
    private final Map<String, Integer> fp;
    private final Map<String, Integer> fn;

    public MultiLabelEvaluation() {
        fn = new HashMap<>();
        fp = new HashMap<>();
        tp = new HashMap<>();
    }

    public void addTp(String label) {
        add(tp, label);
    }

    public void addFp(String label) {
        add(fp, label);
    }

    public void addFn(String label) {
        add(fn, label);
    }

    public List<SpRLEvaluation> getEvaluations() {
        List<SpRLEvaluation> evaluations = new ArrayList<>();
        for (String label : tp.keySet()) {
            SpRLEvaluation eval = new SpRLEvaluation(
                    label,
                    SpRLEvaluator.getPrecision(tp.get(label), fp.get(label)),
                    SpRLEvaluator.getRecall(tp.get(label), fn.get(label)),
                    SpRLEvaluator.getF1(tp.get(label), fp.get(label), fn.get(label)),
                    tp.get(label) + fn.get(label),
                    tp.get(label) + fp.get(label)
            );
            if (eval.getLabeledCount() > 0)
                evaluations.add(eval);
        }
        return evaluations;
    }

    private void add(Map<String, Integer> m, String label) {
        if (label == null)
            return;
        if (!tp.containsKey(label)) {
            tp.put(label, 0);
            fp.put(label, 0);
            fn.put(label, 0);
        }
        m.put(label, m.get(label) + 1);
    }

    public boolean containsLabel(String label) {
        return tp.containsKey(label);
    }
}
