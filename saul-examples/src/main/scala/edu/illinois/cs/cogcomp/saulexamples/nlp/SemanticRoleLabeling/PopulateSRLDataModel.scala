package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import java.util.Properties

import edu.illinois.cs.cogcomp.annotation.AnnotatorException
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, TextAnnotation, TreeView }
import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree
import edu.illinois.cs.cogcomp.core.utilities.configuration.{ Configurator, ResourceManager }
import edu.illinois.cs.cogcomp.curator.{ CuratorConfigurator, CuratorFactory }
import edu.illinois.cs.cogcomp.edison.annotators.ClauseViewGenerator
import edu.illinois.cs.cogcomp.nlp.common.PipelineConfigurator
import edu.illinois.cs.cogcomp.nlp.pipeline.IllinoisPipelineFactory
import edu.illinois.cs.cogcomp.nlp.utilities.ParseUtils
import edu.illinois.cs.cogcomp.saulexamples.data.{ SRLFrameManager, SRLDataReader }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.CommonSensors._
import org.slf4j.{ Logger, LoggerFactory }

import scala.collection.JavaConversions._

/** Created by Parisa on 1/17/16.
  */
object PopulateSRLDataModel {

  def apply[T <: AnyRef](testOnly: Boolean = false, useGoldPredicate: Boolean = false, useGoldArgBoundaries: Boolean = false, rm: ResourceManager = new SRLConfigurator().getDefaultConfig): SRLMultiGraphDataModel = {

    val logger: Logger = LoggerFactory.getLogger(this.getClass)
    val frameManager: SRLFrameManager = new SRLFrameManager(rm.getString(SRLConfigurator.PROPBANK_HOME.key))

    val useCurator = rm.getBoolean(SRLConfigurator.USE_CURATOR)
    val parseViewName = rm.getString(SRLConfigurator.SRL_PARSE_VIEW)
    val graphs: SRLMultiGraphDataModel = new SRLMultiGraphDataModel(parseViewName, frameManager)
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
      case ViewNames.PARSE_GOLD => new ClauseViewGenerator(parseViewName, "CLAUSES_GOLD")
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

    def printNumbers(reader: SRLDataReader, readerType: String) = {
      val numPredicates = reader.textAnnotations.map(ta => ta.getView(ViewNames.SRL_VERB).getConstituents.count(c => c.getLabel == "Predicate")).sum
      val numArguments = reader.textAnnotations.map(ta => ta.getView(ViewNames.SRL_VERB).getConstituents.count(c => c.getLabel != "Predicate")).sum
      logger.debug("Number of {} data predicates: {}", readerType, numPredicates)
      logger.debug("Number of {} data arguments: {}", readerType, numArguments)
    }

    val trainingFromSection = 2
    val trainingToSection = 2
    var gr: SRLMultiGraphDataModel = null
    if (!testOnly) {
      logger.info("Reading training data from sections {} to {}", trainingFromSection, trainingToSection)
      val trainReader = new SRLDataReader(
        rm.getString(SRLConfigurator.TREEBANK_HOME.key),
        rm.getString(SRLConfigurator.PROPBANK_HOME.key),
        trainingFromSection, trainingToSection
      )
      trainReader.readData()
      logger.info("Annotating {} training sentences", trainReader.textAnnotations.size)
      val filteredTa = addViewAndFilter(trainReader.textAnnotations.toList) //.slice(0, 10)
      printNumbers(trainReader, "training")
      logger.info("Populating SRLDataModel with training data.")

      (filteredTa).foreach(a =>
        {
          gr = new SRLMultiGraphDataModel(parseViewName, frameManager)
          if (!useGoldPredicate) {
            gr.sentencesToTokens.addSensor(textAnnotationToTokens _)
            gr.sentences.populate(Seq(a))
            val predicateTrainCandidates = gr.tokens.getTrainingInstances.filter((x: Constituent) => gr.posTag(x).startsWith("VB"))
              .map(c => c.cloneForNewView(ViewNames.SRL_VERB))
            gr.predicates.populate(predicateTrainCandidates)
          } else
            gr.sentences.populate(Seq(a))
          logger.debug("gold relations for this train:" + gr.relations().size)
          if (!useGoldArgBoundaries) {
            val XuPalmerCandidateArgsTraining = gr.predicates.getTrainingInstances.flatMap(x => xuPalmerCandidate(x, (gr.sentences(x.getTextAnnotation) ~> gr.sentencesToStringTree).head))
            gr.sentencesToRelations.addSensor(textAnnotationToRelationMatch _)
            gr.relations.populate(XuPalmerCandidateArgsTraining)
          }
          logger.debug("all relations for this test:" + gr.relations().size)
          graphs.addFromModel(gr)
          if (graphs.sentences().size % 1000 == 0) logger.info("loaded graphs in memory:" + graphs.sentences().size)
        })
    }
    val testSection = rm.getInt(SRLConfigurator.TEST_SECTION)
    val testReader = new SRLDataReader(
      rm.getString(SRLConfigurator.TREEBANK_HOME.key),
      rm.getString(SRLConfigurator.PROPBANK_HOME.key),
      testSection, testSection
    )
    logger.info("Reading test data from section {}", testSection)
    testReader.readData()

    logger.info("Annotating {} test sentences", testReader.textAnnotations.size)
    val filteredTest = addViewAndFilter(testReader.textAnnotations.toList) //.slice(0, 20)

    printNumbers(testReader, "test")

    logger.info("Populating SRLDataModel with test data.")
    (filteredTest).foreach(a => {
      gr = new SRLMultiGraphDataModel(parseViewName, frameManager)
      if (!useGoldPredicate) {
        gr.sentencesToTokens.addSensor(textAnnotationToTokens _)
        gr.sentences.populate(Seq(a), train = false)
        val predicateTestCandidates = gr.tokens.getTestingInstances.filter((x: Constituent) => gr.posTag(x).startsWith("VB"))
          .map(c => c.cloneForNewView(ViewNames.SRL_VERB))
        gr.predicates.populate(predicateTestCandidates, train = false)
      } else
        gr.sentences.populate(Seq(a), train = false)
      logger.debug("gold relations for this test:" + gr.relations().size)
      if (!useGoldArgBoundaries) {
        val XuPalmerCandidateArgsTesting = gr.predicates.getTestingInstances.flatMap(x => xuPalmerCandidate(x, (gr.sentences(x.getTextAnnotation) ~> gr.sentencesToStringTree).head))
        gr.sentencesToRelations.addSensor(textAnnotationToRelationMatch _)
        gr.relations.populate(XuPalmerCandidateArgsTesting, train = false)
      }
      logger.debug("all relations for this test:" + gr.relations().size)
      graphs.addFromModel(gr)
    })
    graphs
  }
}
