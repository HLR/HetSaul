/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.CoNLLColumnFormatReader;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.AbstractSRLAnnotationReader;

import java.io.PrintWriter;
import java.util.*;

/**
 * Prints an SRL predicate-argument view in the CoNLL format so that they can be used by the official evaluation script.
 * Based on the `ColumnFormatWriter` class in `illinois-srl`
 */
public class CoNLLFormatWriter {
    public static void printPredicateArgumentView(PredicateArgumentView pav, PrintWriter out) {
        List<String[]> columns = new ArrayList<>();
        convertPredicateArgView(pav.getTextAnnotation(), pav, columns, false);
        String[][] tr = transpose(columns, pav.getTextAnnotation().size());
        printFormatted(tr, out, pav.getTextAnnotation());
    }

    private static void convertPredicateArgView(TextAnnotation ta,
                                                PredicateArgumentView pav, List<String[]> columns, boolean addSense) {
        List<Constituent> predicates = new ArrayList<>();
        if (pav != null)
            predicates = pav.getPredicates();

        Collections.sort(predicates, TextAnnotationUtilities.constituentStartComparator);

        int size = ta.size();

        addPredicateInfo(columns, predicates, size, addSense);

        for (Constituent predicate : predicates) {
            assert pav != null;
            List<Relation> args = pav.getArguments(predicate);

            String[] paInfo = addPredicateArgInfo(predicate, args, size);

            columns.add(paInfo);
        }
    }

    private static void addPredicateInfo(List<String[]> columns, List<Constituent> predicates, int size, boolean addSense) {
        Map<Integer, String> senseMap = new HashMap<>();
        Map<Integer, String> lemmaMap = new HashMap<>();

        for (Constituent c : predicates) {
            senseMap.put(c.getStartSpan(), c.getAttribute(AbstractSRLAnnotationReader.SenseIdentifier));
            lemmaMap.put(c.getStartSpan(), c.getAttribute(AbstractSRLAnnotationReader.LemmaIdentifier));
        }

        String[] sense = new String[size];
        String[] lemma = new String[size];

        for (int i = 0; i < size; i++) {
            if (lemmaMap.containsKey(i)) {
                sense[i] = senseMap.get(i);
                lemma[i] = lemmaMap.get(i);
            } else {
                sense[i] = "-";
                lemma[i] = "-";
            }
        }

        if (addSense)
            columns.add(sense);
        columns.add(lemma);
    }

    private static String[] addPredicateArgInfo(Constituent predicate, List<Relation> args, int size) {
        Map<Integer, String> paInfo = new HashMap<>();

        paInfo.put(predicate.getStartSpan(), "(V*)");
        for (Relation r : args) {
            String argPredicate = r.getRelationName();

            argPredicate = argPredicate.replaceAll("ARG", "A");
            argPredicate = argPredicate.replaceAll("Support", "SUP");

            for (int i = r.getTarget().getStartSpan(); i < r.getTarget().getEndSpan(); i++) {
                paInfo.put(i, "*");
                if (i == r.getTarget().getStartSpan())
                    paInfo.put(i, "(" + argPredicate + paInfo.get(i));
                if (i == r.getTarget().getEndSpan() - 1)
                    paInfo.put(i, paInfo.get(i) + ")");

            }
        }

        String[] paColumn = new String[size];
        for (int i = 0; i < size; i++) {
            if (paInfo.containsKey(i))
                paColumn[i] = paInfo.get(i);
            else
                paColumn[i] = "*";

        }

        return paColumn;
    }

    private static String[][] transpose(List<String[]> columns, int size) {
        String[][] output = new String[size][];

        for (int i = 0; i < size; i++) {
            output[i] = new String[columns.size()];
        }

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < columns.size(); col++) {
                output[row][col] = columns.get(col)[row];
            }
        }
        return output;
    }

    private static void printFormatted(String[][] columns, PrintWriter out, TextAnnotation ta) {
        // leftOfStar: length of everything before the asterisk.
        // rightOfStar: length of asterisk and what comes after.

        int[] leftOfStar = new int[columns[0].length];
        int[] rightOfStart = new int[columns[0].length];

        for (String[] rowData : columns) {
            for (int col = 0; col < rowData.length; col++) {

                String word = rowData[col];

                int starPos = word.indexOf("*");

                int lenLeft, lenRight;
                if (starPos < 0) {
                    lenLeft = word.length();
                    lenRight = -1;
                } else {
                    lenLeft = starPos + 1;
                    lenRight = word.length() - starPos + 1;
                }

                if (leftOfStar[col] < lenLeft)
                    leftOfStar[col] = lenLeft;

                if (rightOfStart[col] < lenRight)
                    rightOfStart[col] = lenRight;
            }
        }

        assert ta.size() == columns.length;

        for (int sentenceId = 0; sentenceId < ta.getNumberOfSentences(); sentenceId++) {

            int start = ta.getSentence(sentenceId).getStartSpan();

            for (int row = start; row < ta.getSentence(sentenceId).getEndSpan(); row++) {
                String[] rowData = columns[row];

                out.print(rowData[0]);

                // print the spaces
                for (int spCount = rowData[0].length(); spCount < leftOfStar[0]; spCount++)
                    out.print(" ");

                if (rowData.length > 1) {
                    out.print("  " + rowData[1]);

                    // print the spaces
                    for (int spCount = rowData[1].length(); spCount < leftOfStar[1]; spCount++)
                        out.print(" ");

                    out.print("  ");
                }

                for (int colId = 2; colId < rowData.length; colId++) {
                    String word = rowData[colId];
                    int starPos = word.indexOf("*");
                    int leftSpaces, rightSpaces;
                    leftSpaces = leftOfStar[colId];
                    rightSpaces = rightOfStart[colId];

                    if (rightSpaces == 0)
                        leftSpaces = 0;
                    else
                        leftSpaces -= starPos;

                    if (rightSpaces == 0) {
                        rightSpaces = leftOfStar[colId] - word.length();
                    } else {
                        rightSpaces -= (word.length() - starPos);
                    }

                    for (int i = 0; i < leftSpaces - 1; i++)
                        out.print(" ");

                    out.print(word + "  ");

                    for (int i = 0; i < rightSpaces; i++)
                        out.print(" ");

                }
                out.println();
            }
            out.println();
        }
    }
}