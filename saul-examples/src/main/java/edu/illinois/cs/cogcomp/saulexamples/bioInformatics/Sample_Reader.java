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
            o.sampleID = columns[0];
            //  o.patient_id= columns[1];// caled lable-ID
            //  o.GMID=columns[2];
            //  o.WeiID=columns[3];
            //  o.batch=columns[4];
            o.gender = columns[1];
            o.age = columns[2];
            o.ethnicity = columns[3];
            //  o.hidden=columns[8];

            // o.cancer_type = columns[1];
            //o.gender = columns[2];
            //    o.sample_type = columns[3];
            //   o.patient_id = columns[4];
            // o.age_of_diagnosis = (int) Float.parseFloat(columns[5]);
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
            //	print_sample_element(x);
        }
    }
}
