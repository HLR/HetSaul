package edu.illinois.cs.cogcomp.saulexamples.bioInformatics;

import java.util.ArrayList;


public class PatientDrug {
    public String pid="";
    public String drugId="";
    public Double response=0.0;
    public ArrayList<Double> dResponse=new ArrayList<>();

    @Override public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");

        result.append(this.getClass().getName() + " Object {" + NEW_LINE);
        result.append(" pid: " + pid + NEW_LINE);
        result.append(" drugId: " + drugId + NEW_LINE);
        result.append(" response: " + response + NEW_LINE);
        result.append(" dResponse: " + dResponse + NEW_LINE);
        result.append("}");

        return result.toString();
    }
}
