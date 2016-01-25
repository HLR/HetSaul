package edu.illinois.cs.cogcomp.saulexamples.bioInformatics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class drugExampleReader {

    public ArrayList<Patient> patientCollection =new ArrayList<Patient>();
    public ArrayList<PatientGene> samplGExCollection =new ArrayList<PatientGene>();
    public ArrayList<PatientDrug> responseCollection =new ArrayList<PatientDrug>();
    public ArrayList<Patient> joinedPatientCollection =new ArrayList<Patient>();


    public Patient makeElement(String line){
        Patient o=new Patient();
        String[] columns=line.split("\\s+");
        if (columns.length>=4) {
            o.sampleID = columns[0];
         //   o.patient_id= columns[1];// called lable-ID
          //  o.GMID=columns[2];
         //   o.WeiID=columns[3];
         //   o.batch=columns[4];
            o.gender=columns[1];
            o.age=columns[2];
            o.ethnicity=columns[3];
        //    o.hidden=columns[8];

            // o.cancer_type = columns[1];
            //o.gender = columns[2];
            //    o.sample_type = columns[3];
            //   o.patient_id = columns[4];
            // o.age_of_diagnosis = (int) Float.parseFloat(columns[5]);
            return o;
        }else
            return null;
    }

    public ArrayList<PatientDrug> makeDElement(String line){
        ArrayList<PatientDrug> pdList=new ArrayList<PatientDrug>();
        String[] columns=line.split("\t|\n");
        if (columns.length>=6) {
            //  o.dResponse = columns[0];

            for (int i = 1; i <= columns.length - 1; i++)
                {
                    PatientDrug o = new PatientDrug();
                    o.pid = columns[0];
                    o.drugId= "D_"+(i-1);
                    o.response = findDvalue(columns[i]);
                    pdList.add(o);
                }
           /* o.gender = columns[2];
            o.sample_type = scolumns[3];
            o.patient_id = columns[4];
            o.age_of_diagnosis = (int) Float.parseFloat(columns[5]);*/
                return pdList;
        }

            return null;
    }

    public ArrayList<PatientGene> makeGExElement(String line, String[] genenames){
        ArrayList<PatientGene> gp=new ArrayList<PatientGene>();
        String[] columns=line.split("\t|\n");
        if (columns.length>=6) {

            //  o.dResponse = columns[0];

            for (int i=1;i<=columns.length-1;i++){
                PatientGene o=new PatientGene();
                o.sample_ID = columns[0];
                o.Gene_ID= genenames[i];
                o.singleGeneExp=findDvalue(columns[i]);
                gp.add(o);
            }

           /* o.gender = columns[2];
            o.sample_type = scolumns[3];
            o.patient_id = columns[4];
            o.age_of_diagnosis = (int) Float.parseFloat(columns[5]);*/
            return gp;
        }else
            return null;
    }

    private Double findDvalue(String column) {
        if (!column.equals("NA"))
            return  Double.parseDouble(column);

        else
            return (null);
    }
  public ArrayList<PatientDrug> pdReader(String filename2)throws IOException{
      String line="";
      BufferedReader drag_res = new BufferedReader(new FileReader(filename2));
      if (drag_res.ready()){
          line=drag_res.readLine();
      }
      while (drag_res.ready()) {
          line=drag_res.readLine();
         // System.out.println(line);
          if(line.isEmpty()){
              continue;
          }
          ArrayList<PatientDrug> x=makeDElement(line);
          if (x!=null)
              responseCollection.addAll(x);
          //	print_sample_element(x);
      }
      return responseCollection;
  }

  public ArrayList<PatientGene> pgReader(String filename) throws IOException{

      String line="";
      BufferedReader genEx = new BufferedReader(new FileReader(filename));
      if (genEx.ready()){
          line=genEx.readLine();
      }
      String[] geneNames = line.split("\t|\n");
      while (genEx.ready()) {
          line=genEx.readLine();
         // System.out.println(line);
          if(line.isEmpty()){
              continue;
          }
          ArrayList<PatientGene> x=makeGExElement(line,geneNames);
          if (x!=null)
              samplGExCollection.addAll(x);
          //	print_sample_element(x);
      }
      return samplGExCollection;
  }


    public drugExampleReader(){}

    public drugExampleReader(String filename1, String filename2, String filename3) throws IOException {
        String line="";
        BufferedReader clinicalptr = new BufferedReader(new FileReader(filename1));
        if (clinicalptr.ready()){
            line=clinicalptr.readLine();
        }
        while (clinicalptr.ready()) {
            line=clinicalptr.readLine();
          //  System.out.println(line);
            if(line.isEmpty()){
                continue;
            }
            Patient x=makeElement(line);
            if (x!=null)
                patientCollection.add(x);
            //	print_sample_element(x);
        }

        BufferedReader drag_res = new BufferedReader(new FileReader(filename2));
        if (drag_res.ready()){
            line=drag_res.readLine();
        }
        while (drag_res.ready()) {
            line=drag_res.readLine();
            //System.out.println(line);
            if(line.isEmpty()){
                continue;
            }
            ArrayList<PatientDrug> x=makeDElement(line);
            if (x!=null)
                responseCollection.addAll(x);
            //	print_sample_element(x);
        }
         BufferedReader genEx = new BufferedReader(new FileReader(filename3));
         if (genEx.ready()){
             line=genEx.readLine();
         }
        String[] geneNames=line.split("\t|\n");
         while (genEx.ready()) {
             line=genEx.readLine();

            // System.out.println(line);
             if(line.isEmpty()){
                 continue;
             }
            ArrayList<PatientGene> x=makeGExElement(line, geneNames);
             if (x!=null)
                 samplGExCollection.addAll(x);
             //	print_sample_element(x);
         }

         // remove patiens with null data as drag response:
        joinedPatientCollection =joinResponse(patientCollection,responseCollection,samplGExCollection);
         for (int i=0;i< joinedPatientCollection.size();i++) {
             if (joinedPatientCollection.get(i).drag_response==null)
                 joinedPatientCollection.set(i, null);

         }
         joinedPatientCollection.removeAll(Collections.singleton(null));
    }

    private ArrayList<Patient> joinResponse(ArrayList<Patient> patientCollection, ArrayList<PatientDrug> responseCollection, ArrayList<PatientGene> sGExCollection) {
        for (Patient y: patientCollection){
            y.drag_response=find(y,responseCollection);
            y.g_Expression=find2(y, sGExCollection);
       }
        return patientCollection;
    }
    ArrayList<Double> find(Patient y, ArrayList<PatientDrug> l){

        for (PatientDrug x: l) {
            if (y.sampleID.equals(x.pid))
                return x.dResponse;
        }

        System.out.print("The drag response of this patient does not exist.");

        return null;
    }

    ArrayList<Double> find2(Patient y, ArrayList<PatientGene> l){

        for (PatientGene x: l) {
            if (y.sampleID.equals(x.sample_ID))
                return x.gExpression;
        }

        System.out.print("The drag response of this patient does not exist.");

        return null;
    }

}


