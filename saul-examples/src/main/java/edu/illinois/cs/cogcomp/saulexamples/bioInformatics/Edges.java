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

public class Edges {
	static String[] GF_types={
		"GO_term",	//feature	1	1	membership_of_all_GO
		"pfam_domain",//	feature	gene	0	0.1	eValue_of_domain_match
		"motif_u5_gc",//	feature	gene	0	.005,
		"KEGG"	
	};  	
  class KNedge{
    String source_nodeID;
    String sink_nodeID;
    Double edge_weight;
    String edge_type;
  }
  public ArrayList<Gene> geneCollection = new ArrayList<Gene>();
  public  ArrayList<GeneGene>   edgeCollection = new ArrayList<GeneGene>();

  public Edges(String filename) throws IOException {
    BufferedReader edgesptr = new BufferedReader(new FileReader(filename));
    String line="";
    while (edgesptr.ready()) {
      System.out.println(line);
      line=edgesptr.readLine();
      System.out.println(line);
      if(line.isEmpty()){
        continue;
      }
      KNedge x=makeElement(line);
      if (find_type(x.edge_type).equals("Feature"))
        geneCollection.add(makeaGene(x));
      else
        edgeCollection.add(makeaRelation(x));
  }
}
public Gene makeaGene(KNedge x){
  Gene g=new Gene();
  g.GeneID= x.sink_nodeID;
  g.GeneName= x.sink_nodeID;
  switch (x.edge_type) {
    case "GO_term":
      g.GO_term.add(x.source_nodeID);
      break;
      case "KEGG":
      g.KEGG.add(x.source_nodeID);
      break;
      case "motif_u5_gc" :
      g.motif_u5_gc= x.edge_weight;
 }
 return g;
}
public GeneGene makeaRelation(KNedge x)
{
  GeneGene gg = new GeneGene();
  gg.source_nodeID=x.source_nodeID;
  gg.sink_nodeID=x.sink_nodeID;
  if (x.edge_type==null)
    gg.similarity =x.edge_weight;
  else
    switch (x.edge_type) {
      case "PPI_association":
        gg.PPI_association =x.edge_weight.intValue();
        break;
      case "PPI_BioGRID" :
        gg.PPI_BioGRID = x.edge_weight.intValue();

      default :
        gg.similarity =x.edge_weight;
  }
  return gg;
}

public KNedge makeElement(String line){
  KNedge o=new KNedge();
  String[] columns=line.split("\t|\n");

  o.source_nodeID=columns[0];
  o.sink_nodeID=columns[1];
  o.edge_weight= Double.parseDouble(columns[2]);
  if (columns.length>3)
    o.edge_type=columns[3];
  return o;
}
public String find_type(String x){ // This finds a string among an array of strings
   if (x!=null)
    for(int i=0;i<GF_types.length;i++){
     if (GF_types[i].toLowerCase().equals(x.toLowerCase())){
      return "Feature";
    }
  }
  return "Gene";
}
}
