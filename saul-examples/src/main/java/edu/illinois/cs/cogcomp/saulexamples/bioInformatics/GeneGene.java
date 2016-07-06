/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.bioInformatics;

/**
 * Created by Parisa on 1/23/16.
 */
public class GeneGene {
    public String source_nodeID;
    public String sink_nodeID;
    public Double similarity;
    public int PPI_DIP;
    public int PPI_MINT;
    public int PPI_IntAct;
    public int PPI_BioGRID;//     gene    gene    1       1       presence_of_interaction
    public  int PPI_association;// gene    gene    1       3       1=association|2=physical_association|3=direct_interaction
    public int PPI_colocalization;//      gene    gene    1       1       presence_of_interaction
    public int PPI_genetic_interaction;// gene    gene    1       1       presence_of_interaction
    public int STRING_coexpression;//     gene    gene    1       999     STRING_score
    public int STRING_cooccurrance;   //  gene    gene    1       999     STRING_score
    public int STRING_database;// gene    gene    1       999     STRING_score
    public int  STRING_experimental;//     gene    gene    1       999     STRING_score
    public int STRING_fusion;//   gene    gene    1       999     STRING_score
    public int STRING_neighborhood;//     gene    gene    1       999     STRING_score
    public int STRING_textmining;

    @Override public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("GeneGene");
        return result.toString();
    }
}
