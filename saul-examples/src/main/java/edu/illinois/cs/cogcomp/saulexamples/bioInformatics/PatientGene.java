package edu.illinois.cs.cogcomp.saulexamples.bioInformatics;

import java.util.ArrayList;

public class PatientGene {
    public String sample_ID;
    public String Gene_ID;
    public Double singleGeneExp;
    public ArrayList<Double> gExpression=new ArrayList<Double>();
    @Override public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");

        result.append(this.getClass().getName() + " Object {" + NEW_LINE);
        result.append(" sample_ID: " + sample_ID + NEW_LINE);
        result.append(" Gene_ID: " + Gene_ID + NEW_LINE);
        result.append(" singleGeneExp: " + singleGeneExp + NEW_LINE);
        result.append("}");

        return result.toString();
    }
}

