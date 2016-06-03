package edu.illinois.cs.cogcomp.saulexamples.bioInformatics;

import java.util.ArrayList;

public class PatientGene {
    public String sample_ID;
    public String Gene_ID;
    public Double singleGeneExp;
    public ArrayList<Double> gExpression=new ArrayList<Double>();
    @Override public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(Gene_ID);
        return result.toString();
    }
}

