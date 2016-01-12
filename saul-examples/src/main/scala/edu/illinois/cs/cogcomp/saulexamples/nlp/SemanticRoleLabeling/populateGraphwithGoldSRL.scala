package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import java.util.Properties

import edu.illinois.cs.cogcomp.annotation.AnnotatorException
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ TextAnnotation, TreeView }
import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree
import edu.illinois.cs.cogcomp.core.utilities.configuration.{ Configurator, ResourceManager }
import edu.illinois.cs.cogcomp.curator.{ CuratorConfigurator, CuratorFactory }
import edu.illinois.cs.cogcomp.nlp.common.PipelineConfigurator
import edu.illinois.cs.cogcomp.nlp.pipeline.IllinoisPipelineFactory
import edu.illinois.cs.cogcomp.nlp.utilities.ParseUtils
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node
import edu.illinois.cs.cogcomp.saulexamples.ExamplesConfigurator
import edu.illinois.cs.cogcomp.saulexamples.data.SRLDataReader
import org.slf4j.{ Logger, LoggerFactory }

import scala.collection.JavaConversions._

/** Created by Parisa on 12/11/15.
  */

object populateGraphwithGoldSRL extends App {
  import srlDataModel._

  def apply[T <: AnyRef](d: DataModel, x: Node[TextAnnotation]) = {
    val logger: Logger = LoggerFactory.getLogger("populateGraphWithTextAnnotation")

    val rm = new ExamplesConfigurator().getDefaultConfig

    val useCurator = rm.getBoolean(ExamplesConfigurator.USE_CURATOR)
    val annotatorService = useCurator match {
      case true =>
        val nonDefaultProps = new Properties()
        nonDefaultProps.setProperty(CuratorConfigurator.RESPECT_TOKENIZATION.key, Configurator.TRUE)
        CuratorFactory.buildCuratorClient(new CuratorConfigurator().getConfig(new ResourceManager(nonDefaultProps)))
      case false =>
        val nonDefaultProps = new Properties()
        nonDefaultProps.setProperty(PipelineConfigurator.USE_POS.key, Configurator.FALSE)
        nonDefaultProps.setProperty(PipelineConfigurator.USE_NER_CONLL.key, Configurator.FALSE)
        nonDefaultProps.setProperty(PipelineConfigurator.USE_NER_ONTONOTES.key, Configurator.FALSE)
        nonDefaultProps.setProperty(PipelineConfigurator.USE_SHALLOW_PARSE.key, Configurator.FALSE)
        nonDefaultProps.setProperty(PipelineConfigurator.USE_SRL_VERB.key, Configurator.FALSE)
        nonDefaultProps.setProperty(PipelineConfigurator.USE_SRL_NOM.key, Configurator.FALSE)
        nonDefaultProps.setProperty(PipelineConfigurator.USE_STANFORD_DEP.key, Configurator.FALSE)
        nonDefaultProps.setProperty(PipelineConfigurator.USE_STANFORD_PARSE.key, Configurator.FALSE)
        IllinoisPipelineFactory.buildPipeline(new CuratorConfigurator().getConfig(new ResourceManager(nonDefaultProps)))
    }

    def addViewAndFilter(tAll: List[TextAnnotation]): List[TextAnnotation] = {
      var filteredTa = List[TextAnnotation]()
      tAll.foreach(ta => {
        try {
          annotatorService.addView(ta, ViewNames.LEMMA)
          // annotatorService.addView(ta, ViewNames.NER_CONLL)
          // annotatorService.addView(ta, ViewNames.SHALLOW_PARSE)
          // annotatorService.addView(ta, ViewNames.PARSE_STANFORD)
        } catch {
          case e: AnnotatorException =>
            logger.warn("Annotation failed for sentence {}; removing it from the list.", ta.getId)
            tAll.remove(ta)
        }
        // Clean up the trees
        val tree: Tree[String] = ta.getView(ViewNames.PARSE_GOLD).asInstanceOf[TreeView].getTree(0)
        val parseView = new TreeView(ViewNames.PARSE_GOLD, ta)
        parseView.setParseTree(0, ParseUtils.stripFunctionTags(ParseUtils.snipNullNodes(tree)))
        ta.addView(ViewNames.PARSE_GOLD, parseView)
        filteredTa = ta :: filteredTa
      })
      filteredTa
    }

    val trainingFromSection = 2
    val trainingToSection = 3
    logger.info("Reading training data from sections {} to {}", trainingFromSection, trainingToSection)
    val trainReader = new SRLDataReader(
      rm.getString(ExamplesConfigurator.TREEBANK_HOME.key),
      rm.getString(ExamplesConfigurator.PROPBANK_HOME.key),
      trainingFromSection, trainingToSection
    )
    trainReader.readData()

    val testSection = "23"
    val testReader = new SRLDataReader(
      rm.getString(ExamplesConfigurator.TREEBANK_HOME.key),
      rm.getString(ExamplesConfigurator.PROPBANK_HOME.key),
      Array(testSection)
    )
    logger.info("Reading test data from section {}", testSection)
    testReader.readData()

    val filteredTa = addViewAndFilter(trainReader.textAnnotations.slice(0, 10).toList)
    val filteredTest = addViewAndFilter(testReader.textAnnotations.slice(0, 10).toList)

    // Here we populate everything
    x.populate(filteredTa)
    logger.info("Number of SRLDataModel sentences: {}", sentences().size)
    logger.info("Number of SRLDataModel predicates: {}", predicates().size)
    logger.info("Number of SRLDataModel arguments: {}", arguments().size)
    logger.info("Number of SRLDataModel relations: {}", relations().size)

    x.populate(filteredTest, train = false)
    logger.info("Number of SRLDataModel sentences (w/ test data): {}", sentences().size)
  }

}
