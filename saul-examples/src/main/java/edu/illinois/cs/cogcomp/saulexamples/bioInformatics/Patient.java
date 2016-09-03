/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
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
        String NEW_LINE = System.getProperty("line.separator");

        result.append(this.getClass().getName() + " Object {" + NEW_LINE);
        result.append(" patient_id: " + patient_id + NEW_LINE);
        result.append(" gender: " + gender + NEW_LINE);
        result.append(" age: " + age + NEW_LINE);
        result.append(" ethnicity: " + ethnicity + NEW_LINE);
        result.append("}");

        return result.toString();
    }
}
