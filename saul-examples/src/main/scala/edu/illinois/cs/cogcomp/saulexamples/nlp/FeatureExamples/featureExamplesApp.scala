package edu.illinois.cs.cogcomp.saulexamples.nlp.FeatureExamples

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ TextAnnotation, Sentence }
import edu.illinois.cs.cogcomp.core.utilities.ResourceManager
import edu.illinois.cs.cogcomp.curator.CuratorFactory
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.saulexamples.data.{ Document, DocumentReader }
import edu.illinois.cs.cogcomp.saulexamples.nlp.sensors

import scala.collection.JavaConversions._

/** We populate the data and use features in the application below. */
object featureExamplesApp {

  def main(args: Array[String]): Unit = {
    val config = "./saul-examples/config/caching-curator.properties"
    val rm = new ResourceManager(config)
    val annotatorService = CuratorFactory.buildCuratorClient(rm)

    // val annotatorService = IllinoisPipelineFactory.buildPipeline(rm)
    val data: List[Document] = new DocumentReader("./data/20newsToy/train").docs.toList.slice(1, 3)

    val a = sensors.textCollection(data) zip data.map(x => x.getGUID) // this generates a list of strings each member is a textual content of a document
    //    var parserViewEnt:List[Constituent]=List()
    //    var posViewEnt:List[Serializable]=List()
    //    val taList=a.map(x=> CogcompGiantSensor.processDocumentWith(annotatorService,corpus,x._2, x._1))
    // val sentenceList=taList.map(x=>x.sentences()).flatten
    // val ch:TokenLabelView=taList(0).getView(ViewNames.POS).asInstanceOf[TokenLabelView]
    //    taList(0).getConstituents()
    //    sentenceList(0).getConstituents()
    //    getconstituenst of a specific view
    //    EdisonDataModel.addPOviewConstituents()
    //    EdisonDataModel.addSRLviewConstituents()
    //    EdisonDataModel.mergeConstituents(SRLview, Posview)
    //    EdisonDatamodel.addTokenview()
    //    EdisonDataModel.addTokensofxx()
    //    EdisonDataModel.refineTokenview(tokenviewxx)
    //      sentenceList(0).get

    // val chunks2: SpanLabelView = ta.getView(ViewNames.SHALLOW_PARSE).asInstanceOf[SpanLabelView]
    // val parse2: SpanLabelView= ta.getView(ViewNames.NER).asInstanceOf[SpanLabelView]
    //val Pos1: TokenLabelView =ta.getView(ViewNames.POS).asInstanceOf[TokenLabelView]

    //    EdisonDataModel.++(taList)
    //   // EdisonDataModel.++(sentenceList)
    //   //The below line uses a generator sensor
    //    EdisonDataModel.populateWith(util.f,'dTos)
    //    EdisonDataModel.populateWith(util.f2,'tToc)
    // //   EdisonDataModel.populateWith(util.f2,'sToc)

    //The below line uses a matching sensor
    //EdisonDataModel.populateWith(sentenceList,util.alignment,'dTos)

    //TODO: make the below line work, to just use the edge name and depending on the type of sensor a generator or matching edge will be called.
    //EdisonDataModel.populateWith(EdisonDataModel.DocTosen)

    val taa = edisonDataModel.document.getAllInstances
    val sen = edisonDataModel.sentence.getAllInstances
    val constituents = edisonDataModel.chunkConstituents.getAllInstances
    val x1 = edisonDataModel.getFromRelation[Sentence, TextAnnotation](sen.head)
    val x2 = edisonDataModel.getFromRelation[TextAnnotation, Sentence](taa.head)
    // val x3=EdisonDataModel.getFromRelation[TextAnnotation,Constituent](constituents.head)

    println(s"x1.size = ${x1.size}")
    println(s"x2.size = ${x2.size}")

    // Entity.getAddresswithPI[Constituent]("wdfdf",EdisonDataModel.constituents)
    // EdisonDataModel.getEntityWithType[Constituent].getAddresswithPI(x)
    //val filteredConstituent= EdisonDataModel.constituents.filterEntity(EdisonDataModel.Eview,"Token")
    //EdisonDataModel ++ filteredConstituent
    //EdisonDataModel.constituents

    annotatorService.closeCache()
    print("finished")
  }
}
