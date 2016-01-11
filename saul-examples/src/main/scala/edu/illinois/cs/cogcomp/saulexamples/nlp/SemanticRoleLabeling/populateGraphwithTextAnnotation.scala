package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import java.util.Properties

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ TreeView, TextAnnotation }
import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree
import edu.illinois.cs.cogcomp.core.utilities.SerializationHelper
import edu.illinois.cs.cogcomp.core.utilities.configuration.{ ResourceManager, Configurator }
import edu.illinois.cs.cogcomp.curator.{ CuratorConfigurator, CuratorFactory }
import edu.illinois.cs.cogcomp.nlp.pipeline.IllinoisPipelineFactory
import edu.illinois.cs.cogcomp.nlp.utilities.ParseUtils
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node
import edu.illinois.cs.cogcomp.saulexamples.ExamplesConfigurator
import edu.illinois.cs.cogcomp.saulexamples.data.SRLDataReader

import scala.collection.JavaConversions._
import scala.io.Source

/** Created by Parisa on 12/11/15.
  */

object pop {

  def main(args: Array[String]): Unit = {
    aFun()
    val json = Source.fromFile("taJSon.txt").getLines().mkString

    val ta = SerializationHelper.deserializeFromJson(json)
    println(ta.getAvailableViews)
    val annotatorService = CuratorFactory.buildCuratorClient()
    annotatorService.addView(ta, ViewNames.LEMMA)

    //aFun()
  }
  def aFun() = {
    import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlDataModel._
    val rm = new ExamplesConfigurator().getDefaultConfig
    val reader = new SRLDataReader(
      rm.getString(ExamplesConfigurator.TREEBANK_HOME.key),
      rm.getString(ExamplesConfigurator.PROPBANK_HOME.key)
    )
    reader.readData()

    import java.io._
    val pw = new PrintWriter(new File("taJSon.txt"))

    val annotatorService = CuratorFactory.buildCuratorClient()

    val taAll = reader.textAnnotations
    taAll.zipWithIndex.slice(50, 53).foreach {
      case (ta, idx) =>
        println(idx)
        println(ta.getAvailableViews)
        //  try{
        val json = SerializationHelper.serializeToJson(ta)
        println(json)
        pw.write(json.toString())
        // pw.close()
        annotatorService.addView(ta, ViewNames.LEMMA)
        println(ta.getAvailableViews)
      //} catch {
      //	      case _ => println("skipping the annotation! " )
      //    }
    }
    // Here we populate everythingd
    //x.populate(ta.toList)
    pw.close()
    print("size  ", sentences().size)
  }
}

object populateGraphwithTextAnnotation extends App {
  import srlDataModel._

  def apply[T <: AnyRef](d: DataModel, x: Node[TextAnnotation]) = {
    val rm = new ExamplesConfigurator().getDefaultConfig

    val nonDefaultProps = new Properties()
    nonDefaultProps.setProperty(CuratorConfigurator.RESPECT_TOKENIZATION.key, Configurator.TRUE)
    val useCurator = rm.getBoolean(ExamplesConfigurator.USE_CURATOR)
    val annotatorService = useCurator match {
      case true => CuratorFactory.buildCuratorClient(new CuratorConfigurator().getConfig(new ResourceManager(nonDefaultProps)))
      case false => IllinoisPipelineFactory.buildPipeline(new CuratorConfigurator().getConfig(new ResourceManager(nonDefaultProps)))
    }

    def addViewAndFilter(tAll: List[TextAnnotation]): List[TextAnnotation] = {
      var filteredTa = List[TextAnnotation]()
      tAll.zipWithIndex.foreach {
        case (ta, idx) =>
          annotatorService.addView(ta, ViewNames.LEMMA)
          // annotatorService.addView(ta, ViewNames.NER_CONLL)
          // annotatorService.addView(ta, ViewNames.SHALLOW_PARSE)
          // annotatorService.addView(ta, ViewNames.PARSE_STANFORD)
          val tr: Tree[String] = ParseUtils.snipNullNodes(ta.getView(ViewNames.PARSE_GOLD).asInstanceOf[TreeView].getTree(0))
          ta.getView(ViewNames.PARSE_GOLD).asInstanceOf[TreeView].setParseTree(0, tr)

          val parseView = new TreeView(ViewNames.PARSE_GOLD, ta)
          parseView.setParseTree(0, ParseUtils.snipNullNodes(ta.getView(ViewNames.PARSE_GOLD).asInstanceOf[TreeView].getTree(0)))
          ta.addView(ViewNames.PARSE_GOLD, parseView)
          filteredTa = ta :: filteredTa
      }
      filteredTa
    }

    val trainReader = new SRLDataReader(
      rm.getString(ExamplesConfigurator.TREEBANK_HOME.key),
      rm.getString(ExamplesConfigurator.PROPBANK_HOME.key), Array("02") //, "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22")
    )
    trainReader.readData()
    println("Now reading the test")
    val testReader = new SRLDataReader(
      rm.getString(ExamplesConfigurator.TREEBANK_HOME.key),
      rm.getString(ExamplesConfigurator.PROPBANK_HOME.key), Array("23")
    )
    testReader.readData()
    val taAll = trainReader.textAnnotations.slice(0, 10)
    println("all" + taAll.toList.size)
    val filteredTa = addViewAndFilter(taAll.toList)
    print(filteredTa.size)
    val testAll = testReader.textAnnotations //.slice(0, 10)
    val filteredTest = addViewAndFilter(testAll.toList)
    // Here we populate everythingd
    x.populate(filteredTa)
    x.populate(filteredTest, train = false)

    print("size  ", sentences().size)
  }

}
