package edu.illinois.cs.cogcomp.saulexamples.bioInformatics;

import java.util.ArrayList;


public class Patient {
    public String sampleID;
    public String cancer_type;
    public String gender;
    public String sample_type;
    public String patient_id;
    public int age_of_diagnosis;
    public String age;
    public String ethnicity;
    public ArrayList<Double> drag_response=new ArrayList<Double>();
    public ArrayList<Double> g_Expression=new ArrayList<Double>();
    
    @Override public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(patient_id);
        return result.toString();
    }
}
