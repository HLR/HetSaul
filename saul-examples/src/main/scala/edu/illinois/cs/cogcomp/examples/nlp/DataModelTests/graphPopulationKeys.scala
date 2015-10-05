package edu.illinois.cs.cogcomp.examples.nlp.DataModelTests

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Sentence, TextAnnotation }
import edu.illinois.cs.cogcomp.core.utilities.ResourceManager
import edu.illinois.cs.cogcomp.curator.CuratorFactory
//import edu.illinois.cs.cogcomp.examples.nlp
import edu.illinois.cs.cogcomp.examples.sensors
import edu.illinois.cs.cogcomp.tutorial_related.{ Document, DocumentReader }
import scala.collection.JavaConversions._
/**
 * Created by Parisa on 10/4/15.
 */
object graphPopulationKeys {

  def main(args: Array[String]): Unit = {
    val corpus: String = "20-NewsGroup"
    val config = "./saul-examples/config/caching-curator.properties"
    val rm = new ResourceManager(config)
    val annotatorService = CuratorFactory.buildCuratorClient(rm)
    val dat: List[Document] = new DocumentReader("./data/20newsToy/train").docs.toList.slice(1, 3)

    val a = sensors.textCollection(dat) zip dat.map(x => x.getGUID) // this generates a list of strings each member is a textual content of a document
    val taList = a.map(x => sensors.processDocumentWith(annotatorService, corpus, x._2, x._1))
    val sentenceList=taList.map(x=>x.sentences()).flatten
    modelWithKeys.populate(taList)
    modelWithKeys.populate(sentenceList)

    val x1 = modelWithKeys.getFromRelation[Sentence, TextAnnotation](sentenceList(0))
    val x2 = modelWithKeys.getFromRelation[TextAnnotation, Sentence](taList(0))

    println(s"x1.size = ${x1.size}")
    println(s"x2.size = ${x2.size}")

    annotatorService.closeCache()
    print("finished")

  }
}