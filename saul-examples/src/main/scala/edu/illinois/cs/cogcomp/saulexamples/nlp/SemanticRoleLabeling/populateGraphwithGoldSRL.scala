package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import java.util.Properties

import edu.illinois.cs.cogcomp.annotation.AnnotatorException
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ TextAnnotation, TreeView }
import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree
import edu.illinois.cs.cogcomp.core.utilities.configuration.{ Configurator, ResourceManager }
import edu.illinois.cs.cogcomp.curator.{ CuratorConfigurator, CuratorFactory }
import edu.illinois.cs.cogcomp.edison.annotators.ClauseViewGenerator
import edu.illinois.cs.cogcomp.nlp.common.PipelineConfigurator
import edu.illinois.cs.cogcomp.nlp.pipeline.IllinoisPipelineFactory
import edu.illinois.cs.cogcomp.nlp.utilities.ParseUtils
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node
import edu.illinois.cs.cogcomp.saulexamples.ExamplesConfigurator
import edu.illinois.cs.cogcomp.saulexamples.data.SRLDataReader
import org.slf4j.{ Logger, LoggerFactory }

import scala.collection.JavaConversions._

/** Fetches the training/test data, does the required preprocessing and populates the `srlDataModel`
  * @author Parisa Kordjamshidi
  * @author Christos Christodoulopoulos
  */

object populateGraphwithGoldSRL extends App {
  import srlDataModel._

  def apply[T <: AnyRef](d: DataModel, x: Node[TextAnnotation]) = {
    val logger: Logger = LoggerFactory.getLogger(this.getClass)

    val rm = new ExamplesConfigurator().getDefaultConfig

    val useCurator = rm.getBoolean(ExamplesConfigurator.USE_CURATOR)
    val parseViewName = rm.getString(ExamplesConfigurator.SRL_PARSE_VIEW)

    val annotatorService = useCurator match {
      case true =>
        val nonDefaultProps = new Properties()
        nonDefaultProps.setProperty(CuratorConfigurator.RESPECT_TOKENIZATION.key, Configurator.TRUE)
        CuratorFactory.buildCuratorClient(new CuratorConfigurator().getConfig(new ResourceManager(nonDefaultProps)))
      case false =>
        val nonDefaultProps = new Properties()
        if (parseViewName.equals(ViewNames.PARSE_GOLD))
          nonDefaultProps.setProperty(PipelineConfigurator.USE_POS.key, Configurator.FALSE)
        nonDefaultProps.setProperty(PipelineConfigurator.USE_NER_CONLL.key, Configurator.FALSE)
        nonDefaultProps.setProperty(PipelineConfigurator.USE_NER_ONTONOTES.key, Configurator.FALSE)
        nonDefaultProps.setProperty(PipelineConfigurator.USE_SRL_VERB.key, Configurator.FALSE)
        nonDefaultProps.setProperty(PipelineConfigurator.USE_SRL_NOM.key, Configurator.FALSE)
        nonDefaultProps.setProperty(PipelineConfigurator.USE_STANFORD_DEP.key, Configurator.FALSE)
        if (parseViewName.equals(ViewNames.PARSE_GOLD))
          nonDefaultProps.setProperty(PipelineConfigurator.USE_STANFORD_PARSE.key, Configurator.FALSE)
        IllinoisPipelineFactory.buildPipeline(new CuratorConfigurator().getConfig(new ResourceManager(nonDefaultProps)))
    }
    val clauseViewGenerator = parseViewName match {
      case ViewNames.PARSE_GOLD => new ClauseViewGenerator (parseViewName, "CLAUSES_GOLD")
      case ViewNames.PARSE_STANFORD => ClauseViewGenerator.STANFORD
    }

    def addViewAndFilter(tAll: List[TextAnnotation]): List[TextAnnotation] = {
      var filteredTa = List[TextAnnotation]()
      tAll.foreach(ta => {
        try {
          annotatorService.addView(ta, ViewNames.LEMMA)
          annotatorService.addView(ta, ViewNames.SHALLOW_PARSE)
          if (!parseViewName.equals(ViewNames.PARSE_GOLD)) {
            annotatorService.addView(ta, ViewNames.POS)
            annotatorService.addView(ta, ViewNames.PARSE_STANFORD)
          }
          // Add a clause view (needed for the clause relative position feature)
          clauseViewGenerator.addView(ta)
        } catch {
          case e: AnnotatorException =>
            logger.warn("Annotation failed for sentence {}; removing it from the list.", ta.getId)
            tAll.remove(ta)
        }
        // Clean up the trees
        val tree: Tree[String] = ta.getView(parseViewName).asInstanceOf[TreeView].getTree(0)
        val parseView = new TreeView(parseViewName, ta)
        parseView.setParseTree(0, ParseUtils.stripFunctionTags(ParseUtils.snipNullNodes(tree)))
        ta.addView(parseViewName, parseView)
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

    val testSection = 23
    val testReader = new SRLDataReader(
      rm.getString(ExamplesConfigurator.TREEBANK_HOME.key),
      rm.getString(ExamplesConfigurator.PROPBANK_HOME.key),
      testSection, testSection
    )
    logger.info("Reading test data from section {}", testSection)
    testReader.readData()

    logger.info("Annotating {} training sentences", trainReader.textAnnotations.size)
    val filteredTa = addViewAndFilter(trainReader.textAnnotations.toList)
    logger.info("Annotating {} test sentences", testReader.textAnnotations.size)
    val filteredTest = addViewAndFilter(testReader.textAnnotations.toList)

    def printNumbers(reader: SRLDataReader, readerType: String) = {
      val numPredicates = reader.textAnnotations.map(ta => ta.getView(ViewNames.SRL_VERB).getConstituents.count(c => c.getLabel == "Predicate")).sum
      val numArguments = reader.textAnnotations.map(ta => ta.getView(ViewNames.SRL_VERB).getConstituents.count(c => c.getLabel != "Predicate")).sum
      logger.debug("Number of {} data predicates: {}", readerType, numPredicates)
      logger.debug("Number of {} data arguments: {}", readerType, numArguments)
    }
    printNumbers(trainReader, "training")
    printNumbers(testReader, "test")

    // Here we populate everything
    logger.info("Populating SRLDataModel with training data.")
    x.populate(filteredTa)
    logger.info("Number of SRLDataModel sentences: {}", sentences().size)
    logger.debug("Number of SRLDataModel predicates: {}", predicates().size)
    logger.debug("Number of SRLDataModel arguments: {}", arguments().size)
    logger.debug("Number of SRLDataModel relations: {}", relations().size)

    logger.info("Populating SRLDataModel with test data.")
    x.populate(filteredTest, train = false)
    logger.info("Number of SRLDataModel sentences (w/ test data): {}", sentences().size)
    logger.debug("Number of SRLDataModel predicates (w/ test data): {}", predicates().size)
    logger.debug("Number of SRLDataModel arguments (w/ test data): {}", arguments().size)
    logger.debug("Number of SRLDataModel relations (w/ test data): {}", relations().size)

  }

}
