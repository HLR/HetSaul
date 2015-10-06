package edu.illinois.cs.cogcomp.saulexamples.nlp.FeatureExamples

import edu.illinois.cs.cogcomp.core.datastructures.textannotation._
import edu.illinois.cs.cogcomp.core.utilities.ResourceManager
import edu.illinois.cs.cogcomp.curator.CuratorFactory
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel._
import edu.illinois.cs.cogcomp.saulexamples.data.{ Document, DocumentReader }
import edu.illinois.cs.cogcomp.saulexamples.nlp.sensors

import scala.collection.JavaConversions._
import scala.collection.mutable.{ Map => MutableMap }

object EdisonDataModel extends DataModel {

  /** Node Types
    */
  val document = node[TextAnnotation] //(
  // PrimaryKey = {
  //   t:TextAnnotation => t.getId}//.hashCode().toString}
  // )

  val sentence = node[Sentence] //(
  //    PrimaryKey = {
  //      t: Sentence => t.hashCode().toString
  //   }
  //   ,
  //    SecondaryKeyMap = MutableMap(
  //      'dTos -> ((t: Sentence) => t.getSentenceConstituent.getTextAnnotation.getId)
  //    )
  //  )

  val relations = node[Relation]

  val Chunk_constituents = node[Constituent] //(//example of adding keys we can change this
  //    PrimaryKey = {
  //      t: Constituent => String.valueOf(t.hashCode)
  //    },
  //    SecondaryKeyMap = MutableMap(
  //      'sentenceId -> ((t: Constituent) => String.valueOf(t.getTextAnnotation.getId)),
  //      'addressId -> ((t: Constituent) => String.valueOf(t.getSpan.toString))
  //    )
  //  )

  /** Property Types
    */

  val label = discreteAttributeOf[Constituent]('rubish) {
    x =>
      {
        x.getLabel
      }
  }

  val Eaddress = discreteAttributeOf[Constituent]('address) {
    x => x.getSpan.toString
  }

  val Eview = discreteAttributeOf[Constituent]('CviewName) {
    x => x.getViewName
  }

  val Rveiw = discreteAttributeOf[Relation]('RveiwName) {
    x: Relation =>
      {
        x.getSource.getViewName
      }
  }
  val DocFeatureExample = discreteAttributeOf[TextAnnotation]('doc) {
    x: TextAnnotation =>
      {
        x.getNumberOfSentences.toString
      }
  }
  val sentenceFeatureExample = discreteAttributeOf[Sentence]('sentnce) {
    x: Sentence =>
      {
        x.getText
      }
  }
  val NODES = ~~(document, sentence, Chunk_constituents)

  /** Edge Types
    */

  val DocTosen = edge[TextAnnotation, Sentence]('dTos) //(PID,'dTos2)// {
  //   ta: TextAnnotation => ta.sentences()
  //}
  val SenToCons = edge[TextAnnotation, Constituent]('tToc)
  //val DocTosen=edge[TextAnnotation,Sentence](util.f: TextAnnotation => List[Sentence])('dTos)
  // val DocTosen=edge[TextAnnotation,Sentence]('dTos)

  // val SentoCons=edge[Sentence,Constituent]('sToC)//todo complete definition
  // val ConstToConst=edge[Constituent,Constituent]('cRc)(PID===PID) //TODO set connections

  // val DocTosen=edge[TextAnnotation,Sentence](util.f: TextAnnotation => List[Sentence])('dTos2)// oneToMany

  val PROPERTIES = List(Eview, Rveiw)
  val EDGES = DocTosen
}

/** We populate the data and use features in the application below. */
object myapp {

  def main(args: Array[String]): Unit = {
    val corpus: String = "20-NewsGroup"
    val x: Constituent = null
    val config = "./saul-examples/config/caching-curator.properties"
    val rm = new ResourceManager(config)
    val annotatorService = CuratorFactory.buildCuratorClient(rm)
    //
    // val annotatorService = IllinoisPipelineFactory.buildPipeline(rm)
    val dat: List[Document] = new DocumentReader("./data/20newsToy/train").docs.toList.slice(1, 3)

    val a = sensors.textCollection(dat) zip dat.map(x => x.getGUID) // this generates a list of strings each member is a textual content of a document
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

    val taa = EdisonDataModel.document.getAllInstances
    val sen = EdisonDataModel.sentence.getAllInstances
    val constituents = EdisonDataModel.Chunk_constituents.getAllInstances
    val x1 = EdisonDataModel.getFromRelation[Sentence, TextAnnotation](sen.head)
    val x2 = EdisonDataModel.getFromRelation[TextAnnotation, Sentence](taa.head)
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
