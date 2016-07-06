/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.bioInformatics;

import java.util.ArrayList;

/**
 * Created by Parisa on 1/22/16.
 */
public class Gene {
    public String GeneID;
    public String GeneName;
    public ArrayList<String> GO_term=new ArrayList<String>();
    public Double pfam_domain=0.0;
    public Double motif_u5_gc=0.0; //	feature	gene	0	.005,
    public ArrayList<String> KEGG = new ArrayList<String>();

    @Override public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(GeneID);
        return result.toString();
    }
}
