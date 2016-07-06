/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.bioInformatics;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Sample_Reader {


    public ArrayList<Patient> patientCollection = new ArrayList<Patient>();

    public Patient makeElement(String line) {
        Patient o = new Patient();
        String[] columns = line.split("\t|\n");
        if (columns.length >= 4) {
            o.patient_id = columns[0];
            o.gender = columns[5];
            if(columns[6].equals("NA")){
                o.age = "0";
            }else{
                o.age = columns[6];
            }
            o.ethnicity = columns[7];
            return o;
        } else
            return null;
    }

    public Sample_Reader(String filename1) throws IOException {
        String line = "";
        BufferedReader clinicalptr = new BufferedReader(new FileReader(filename1));
        if (clinicalptr.ready()) {
            line = clinicalptr.readLine();
        }
        while (clinicalptr.ready()) {
            line = clinicalptr.readLine();
            System.out.println(line);
            if (line.isEmpty()) {
                continue;
            }
            Patient x = makeElement(line);
            if (x != null)
                patientCollection.add(x);
        }
    }
}
