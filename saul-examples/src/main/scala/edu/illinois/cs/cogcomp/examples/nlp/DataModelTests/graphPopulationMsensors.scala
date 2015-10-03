package edu.illinois.cs.cogcomp.examples.nlp.DataModelTests

/** Created by Parisa on 10/2/15.
  */

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{Sentence, TextAnnotation}
import edu.illinois.cs.cogcomp.core.utilities.ResourceManager
import edu.illinois.cs.cogcomp.curator.CuratorFactory
import edu.illinois.cs.cogcomp.examples.nlp.FeatureExamples.{CogcompGiantSensor, EdisonDataModel}
import edu.illinois.cs.cogcomp.examples.sensors
import edu.illinois.cs.cogcomp.tutorial_related.{Document, DocumentReader}

import scala.collection.JavaConversions._

object graphPopulationMsensors {
      def main(args: Array[String]): Unit = {
      val corpus: String = "20-NewsGroup"
      val config = "./saul-examples/config/caching-curator.properties"
      val rm = new ResourceManager(config)
      val annotatorService = CuratorFactory.buildCuratorClient(rm)
      val dat: List[Document] = new DocumentReader("./data/20newsToy/train").docs.toList.slice(0, 2)

      val a = sensors.textCollection(dat) zip dat.map(x => x.getGUID) // this generates a list of strings each member is a textual content of a document
      val taList = a.map(x => CogcompGiantSensor.processDocumentWith(annotatorService, corpus, x._2, x._1))
      val sentenceList=taList.map(x=>x.sentences()).flatten

      EdisonDataModel.++(taList)
      EdisonDataModel.populateWith(sentenceList,sensors.alignment,'dTos)

      val taa = EdisonDataModel.document.getAllInstances
      val sen = EdisonDataModel.sentence.getAllInstances
      val x1 = EdisonDataModel.getFromRelation[Sentence, TextAnnotation](sen.head)
      val x2 = EdisonDataModel.getFromRelation[TextAnnotation, Sentence](taa.head)

      println(s"x1.size = ${x1.size}")
      println(s"x2.size = ${x2.size}")

      annotatorService.closeCache()
      print("finished")
  }


}
