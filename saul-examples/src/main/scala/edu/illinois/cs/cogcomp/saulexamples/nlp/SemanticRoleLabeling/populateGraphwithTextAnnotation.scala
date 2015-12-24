package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ TreeView, TextAnnotation }
import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree
import edu.illinois.cs.cogcomp.core.utilities.SerializationHelper
import edu.illinois.cs.cogcomp.curator.CuratorFactory
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
    import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLDataModel._
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

  def apply[T <: AnyRef](d: DataModel, x: Node[TextAnnotation]) = {
    val annotatorService = CuratorFactory.buildCuratorClient()
    import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLDataModel_all._
    def addViewAndFilter(tAll: List[TextAnnotation]): List[TextAnnotation] = {
      var filteredTa = List[TextAnnotation]()
      tAll.foreach { ta =>
        println(ta.getAvailableViews)
        try {
          annotatorService.addView(ta, ViewNames.LEMMA)
          annotatorService.addView(ta, ViewNames.TOKENS)
          annotatorService.addView(ta, ViewNames.POS)
          annotatorService.addView(ta, ViewNames.NER_CONLL)
          annotatorService.addView(ta, ViewNames.SHALLOW_PARSE)
          annotatorService.addView(ta, ViewNames.PARSE_STANFORD)
          val tr: Tree[String] = ParseUtils.snipNullNodes(ta.getView(ViewNames.PARSE_GOLD).asInstanceOf[TreeView].getTree(0))
          ta.getView(ViewNames.PARSE_GOLD).asInstanceOf[TreeView].setParseTree(0, tr)
          filteredTa = ta :: filteredTa
        } catch {
          case _ => {
            println("skipping the annotation! ")
          }
          // taAll.remove(ta)
        }
      }
      filteredTa
    }

    val rm = new ExamplesConfigurator().getDefaultConfig
    val trainReader = new SRLDataReader(
      rm.getString(ExamplesConfigurator.TREEBANK_HOME.key),
      rm.getString(ExamplesConfigurator.PROPBANK_HOME.key), Array("00")
    )
    trainReader.readData()

    val testReader = new SRLDataReader(
      rm.getString(ExamplesConfigurator.TREEBANK_HOME.key),
      rm.getString(ExamplesConfigurator.PROPBANK_HOME.key), Array("01")
    )
    testReader.readData()
    val taAll = trainReader.textAnnotations //.slice(0,5)
    println("all" + taAll.toList.size)
    val filteredTa = addViewAndFilter(taAll.toList)
    print(filteredTa.size)
    // addViewToCollection(taAll.asInstanceOf[ListBuffer[TextAnnotation]])

    val testAll = testReader.textAnnotations //.slice(0,5)

    val filteredTest = addViewAndFilter(testAll.toList)

    //addViewToCollection(taAll.toList)
    //   addViewToCollection(testAll.asInstanceOf[ListBuffer[TextAnnotation]])

    //    for (t <- taAll) {
    //      println(t.getAvailableViews)
    //      annotatorService.addView(t, ViewNames.LEMMA)
    //      println("instance number" + i)
    //      if (i==90)
    //        print("STOP")
    //    }

    // Here we populate everythingd
    x.populate(filteredTa)
    x.populate(filteredTest, train = false)

    print("size  ", sentences().size)
  }

}
