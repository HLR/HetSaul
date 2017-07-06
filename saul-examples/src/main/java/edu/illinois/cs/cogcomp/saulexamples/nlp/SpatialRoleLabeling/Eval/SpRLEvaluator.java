package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Eval;


import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;

/**
 * Created by Taher on 2016-09-20.
 */
public class SpRLEvaluator {

    public static void printEvaluation(String caption, List<SpRLEvaluation> eval) {
        printEvaluation(caption, System.out, eval);
    }

    public static void printEvaluation(List<SpRLEvaluation> eval) {
        printEvaluation(System.out, eval);
    }

    public static void printEvaluation(String caption, OutputStream outputStream, List<SpRLEvaluation> eval) {
        PrintStream out = new PrintStream(outputStream, true);
        out.println(repeat("-", 75));
        out.println("-  " + caption);
        out.println(repeat("-", 75));
        printEvaluation(outputStream, eval);
    }

    public static void printEvaluation(OutputStream outputStream, List<SpRLEvaluation> eval) {
        PrintStream out = new PrintStream(outputStream, true);
        out.printf("%-20s %-10s %-10s %-10s %-10s %-10s\n",
                "label",
                "Precision",
                "Recall",
                "F1",
                "LCount",
                "PCount"
        );
        out.println(repeat("-", 75));
        for (SpRLEvaluation e : eval) {
            out.printf("%-20s %-10.3f %-10.3f %-10.3f %-10d %-10d\n",
                    e.getLabel(),
                    e.getPrecision(),
                    e.getRecall(),
                    e.getF1(),
                    e.getLabeledCount(),
                    e.getPredictedCount()
            );
        }
        printOverall(outputStream, eval);
    }

    public static void printOverall(OutputStream outputStream, List<SpRLEvaluation> eval) {
        PrintStream out = new PrintStream(outputStream, true);
        out.println(repeat("-", 75));
        SpRLEvaluation e = getOverall(eval);
        out.printf("%-20s %-10.3f %-10.3f %-10.3f %-10d %-10d\n",
                "Overall",
                e.getPrecision(),
                e.getRecall(),
                e.getF1(),
                e.getLabeledCount(),
                e.getPredictedCount()
        );
        out.println(repeat("-", 75));
    }

    public static SpRLEvaluation getOverall(List<SpRLEvaluation> evals) {
        double precision = 0, recall = 0, f1 = 0;
        int labeledCount = 0, predictedCount = 0;
        for (SpRLEvaluation e : evals) {
            precision += e.getLabeledCount() * e.getPrecision();
            recall += e.getLabeledCount() * e.getRecall();
            f1 += e.getLabeledCount() * e.getF1();
            labeledCount += e.getLabeledCount();
            predictedCount += e.getPredictedCount();
        }
        if (labeledCount > 0) {
            precision /= labeledCount;
            recall /= labeledCount;
            f1 /= labeledCount;
        }
        return new SpRLEvaluation("Overall", precision, recall, f1, labeledCount, predictedCount);
    }

    public List<SpRLEvaluation> evaluateRoles(RolesEvalDocument actual, RolesEvalDocument predicted) {
        return evaluateRoles(actual, predicted, new ExactComparer());
    }

    public List<SpRLEvaluation> evaluateRoles(RolesEvalDocument actual, RolesEvalDocument predicted, EvalComparer comparer) {

        List<SpRLEvaluation> evaluations = new ArrayList<>();

        evaluations.add(evaluate("SP", actual.getSpatialIndicators(), predicted.getSpatialIndicators(), comparer));
        evaluations.add(evaluate("TR", actual.getTrajectors(), predicted.getTrajectors(), comparer));
        evaluations.add(evaluate("LM", actual.getLandmarks(), predicted.getLandmarks(), comparer));

        return evaluations;
    }

    public List<SpRLEvaluation> evaluateRelations(RelationsEvalDocument actual, RelationsEvalDocument predicted) {
        return evaluateRelations(actual, predicted, new ExactComparer());
    }

    public List<SpRLEvaluation> evaluateRelations(RelationsEvalDocument actual, RelationsEvalDocument predicted,
                                                  EvalComparer comparer) {

        List<SpRLEvaluation> evaluations = new ArrayList<>();
        evaluations.add(evaluate("Relation", actual.getRelations(), predicted.getRelations(), comparer));

        return evaluations;
    }

    public List<SpRLEvaluation> evaluateRelationGeneralType(SpRLEvaluation relationsEval) {
        return evaluateRelationType(relationsEval, (e, a) -> toUpper(e.getGeneralType()));
    }

    public List<SpRLEvaluation> evaluateRelationSpecificType(SpRLEvaluation relationsEval) {
        return evaluateRelationType(relationsEval, (e, isActual) -> toUpper(e.getSpecificType()));
    }

    public List<SpRLEvaluation> evaluateRelationRCC8(SpRLEvaluation relationsEval) {
        return evaluateRelationType(relationsEval, (e, isActual) -> toUpper(e.getRCC8()));
    }

    public List<SpRLEvaluation> evaluateRelationFoR(SpRLEvaluation relationsEval) {
        return evaluateRelationType(relationsEval, (e, a) -> toUpper(e.getFoR()));
    }

    private List<SpRLEvaluation> evaluateRelationType(SpRLEvaluation relationsEval, RelationTypeExtractor extractor) {
        MultiLabelEvaluation evaluation = new MultiLabelEvaluation();

        for (SpRLEval e : relationsEval.getTp().keySet()) {
            RelationEval a = (RelationEval) e;
            RelationEval p = (RelationEval) relationsEval.getTp().get(e);
            String aType = extractor.getType(a, true);
            String pType = extractor.getType(p, false);
            if (aType != null) {
                if (aType.equals(pType)) {
                    evaluation.addTp(aType);
                } else {
                    evaluation.addFn(aType);
                    evaluation.addFp(pType);
                }
            }
        }

        for (SpRLEval e : relationsEval.getFn()) {
            RelationEval re = (RelationEval) e;
            evaluation.addFn(extractor.getType(re, true));
        }

        for (SpRLEval e : relationsEval.getFp()) {
            RelationEval re = (RelationEval) e;
            if (evaluation.containsLabel(extractor.getType(re, false)))
                evaluation.addFp(extractor.getType(re, false));
        }

        return evaluation.getEvaluations();
    }


    private <T extends SpRLEval> SpRLEvaluation evaluate(String label, List<T> actualList, List<T> predictedList,
                                                         EvalComparer comparer) {
        int tp = 0;
        List<T> fpList = new ArrayList<>();
        List<T> fnList = new ArrayList<>();
        Map<T, T> tpList = new HashMap<>();
        List<T> actual = distinct(actualList);
        List<T> predicted = distinct(predictedList);
        int predictedCount = predicted.size();
        int actualCount = actual.size();

        for (T a : actual) {
            boolean labeled = false;
            for (T p : predicted) {
                if (comparer.isEqual(a, p)) {
                    tp++;
                    tpList.put(a, p);
                    predicted.remove(p);
                    labeled = true;
                    break;
                }
            }
            if (!labeled) {
                fnList.add(a);
            }
        }
        fpList.addAll(predicted);

        int fp = predictedCount - tp;
        int fn = actualCount - tp;
        double precision = getPrecision(tp, fp);
        double recall = getRecall(tp, fn);
        double f1 = getF1(tp, fp, fn);


        SpRLEvaluation evaluation = new SpRLEvaluation(
                label,
                precision,
                recall,
                f1,
                actualCount,
                predictedCount
        );
        evaluation.getFn().addAll(fnList);
        evaluation.getFp().addAll(fpList);
        evaluation.getTp().putAll(tpList);
        return evaluation;
    }

    public static double getF1(int tp, int fp, int fn) {
        double precision = getPrecision(tp, fp);
        double recall = getRecall(tp, fn);
        return precision == 0 || recall == 0 ? 0 : 2 * precision * recall / (precision + recall);
    }

    public static double getRecall(int tp, double fn) {
        return tp == 0 ? (fn == 0 ? 100 : 0) : (double) tp / (tp + fn) * 100;
    }

    public static double getPrecision(int tp, int fp) {
        return tp == 0 ? (fp == 0 ? 100 : 0) : (double) tp / (tp + fp) * 100;
    }

    private <T extends SpRLEval> List<T> distinct(List<T> l) {
        HashSet<T> set = new HashSet<T>();
        List<T> newList = new ArrayList<T>();
        set.add(l.get(0));
        for (T i : l) {
            if (!set.contains(i))
                set.add(i);
        }
        newList.addAll(set);
        return newList;
    }

    private static String repeat(String s, int n) {
        String str = "";
        for (int i = 0; i < n; i++)
            str += s;
        return str;
    }

    private static String toUpper(String s) {
        return s == null ? null : s.toUpperCase();
    }

}
