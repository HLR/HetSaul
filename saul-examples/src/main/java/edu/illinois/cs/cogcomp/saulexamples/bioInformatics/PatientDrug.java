package edu.illinois.cs.cogcomp.saulexamples.bioInformatics;

import java.util.ArrayList;


public class PatientDrug {
    public String pid="";
    public String drugId="";
    public Double response=0.0;
    public ArrayList<Double> dResponse=new ArrayList<>();
    
    @Override public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(drugId);
        return result.toString();
    }
}
