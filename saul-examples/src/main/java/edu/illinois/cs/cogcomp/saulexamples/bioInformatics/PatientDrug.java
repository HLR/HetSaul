/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
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
