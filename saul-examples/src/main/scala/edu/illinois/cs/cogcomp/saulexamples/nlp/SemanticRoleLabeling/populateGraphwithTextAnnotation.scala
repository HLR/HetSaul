package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node
import edu.illinois.cs.cogcomp.saulexamples.ExamplesConfigurator
import edu.illinois.cs.cogcomp.saulexamples.data.SRLDataReader

import scala.collection.JavaConversions._
/**
 * Created by Parisa on 12/11/15.
 */
object populateGraphwithTextAnnotation extends App{
 def apply[T<:AnyRef](d: DataModel, x: Node[TextAnnotation]) ={
   import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLDataModel._
   val rm = new ExamplesConfigurator().getDefaultConfig
   val reader = new SRLDataReader(
     rm.getString(ExamplesConfigurator.TREEBANK_HOME.key),
     rm.getString(ExamplesConfigurator.PROPBANK_HOME.key)
   )
   reader.readData()

   // Here we populate everything
   x.populate(reader.textAnnotations.toList)
   print("size  ",sentences().size)
 }


}
