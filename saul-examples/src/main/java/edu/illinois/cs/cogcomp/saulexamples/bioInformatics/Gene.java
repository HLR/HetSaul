package edu.illinois.cs.cogcomp.saulexamples.bioInformatics;

import java.util.ArrayList;

/**
 * Created by Parisa on 1/22/16.
 */
public class Gene {
    public String GeneID;
    public String GeneName;
    public ArrayList<String> GO_term=new ArrayList<String>();
    public Double pfam_domain;
    public Double motif_u5_gc; //	feature	gene	0	.005,
    public ArrayList<String> KEGG = new ArrayList<String>();
}
