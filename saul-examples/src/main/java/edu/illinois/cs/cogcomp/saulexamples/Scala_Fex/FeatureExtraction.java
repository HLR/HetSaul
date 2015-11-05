package edu.illinois.cs.cogcomp.saulexamples.Scala_Fex;

import edu.illinois.cs.cogcomp.saulexamples.nlp.DataModelTests.graphForFex;

public class FeatureExtraction {
    public static void main(String[] args) {
        System.out.println("These queries are called from Saul's datamodel!");
        if (graphForFex.query2().iterator().hasNext())
            System.out.println("Prefix:" + graphForFex.query2().iterator().next());
        System.out.println("Prefix:" + graphForFex.query1());
    }
}
