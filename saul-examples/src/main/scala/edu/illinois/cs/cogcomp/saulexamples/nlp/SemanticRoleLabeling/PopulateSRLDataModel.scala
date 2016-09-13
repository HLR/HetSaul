/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import java.util.Properties

import edu.illinois.cs.cogcomp.annotation.AnnotatorException
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, TextAnnotation, TreeView }
import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager
import edu.illinois.cs.cogcomp.curator.CuratorConfigurator._
import edu.illinois.cs.cogcomp.curator.{ CuratorConfigurator, CuratorFactory }
import edu.illinois.cs.cogcomp.edison.annotators.ClauseViewGenerator
import edu.illinois.cs.cogcomp.nlp.common.PipelineConfigurator._
import edu.illinois.cs.cogcomp.nlp.utilities.ParseUtils
import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.data.{ SRLDataReader, SRLFrameManager }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.CommonSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.TextAnnotationFactory

import scala.collection.JavaConversions._

/** Created by Parisa on 1/17/16.
  */
object PopulateSRLDataModel extends Logging {

  def apply[T <: AnyRef](
    testOnly: Boolean = false,
    useGoldPredicate: Boolean = false,
    useGoldArgBoundaries: Boolean = false,
    rm: ResourceManager = new SRLConfigurator().getDefaultConfig
  ): SRLMultiGraphDataModel = {

    val frameManager: SRLFrameManager = new SRLFrameManager(rm.getString(SRLConfigurator.PROPBANK_HOME.key))
    val useCurator = rm.getBoolean(SRLConfigurator.USE_CURATOR)
    val parseViewName = rm.getString(SRLConfigurator.SRL_PARSE_VIEW)
    val graphs: SRLMultiGraphDataModel = new SRLMultiGraphDataModel(parseViewName, frameManager)
    val annotatorService = useCurator match {
      case true =>
        val nonDefaultProps = new Properties()
        TextAnnotationFactory.enableSettings(nonDefaultProps, RESPECT_TOKENIZATION)
        TextAnnotationFactory.createCuratorAnnotatorService(nonDefaultProps)
      case false =>
        val nonDefaultProps = new Properties()
        TextAnnotationFactory.disableSettings(nonDefaultProps, USE_NER_CONLL, USE_NER_ONTONOTES, USE_SRL_VERB, USE_SRL_NOM, USE_STANFORD_DEP)
        if (parseViewName.equals(ViewNames.PARSE_GOLD))
          TextAnnotationFactory.disableSettings(nonDefaultProps, USE_POS, USE_STANFORD_PARSE)
        TextAnnotationFactory.createPipelineAnnotatorService(nonDefaultProps)
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
            logger.warn(s"Annotation failed for sentence ${ta.getId}; removing it from the list.")
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
      logger.debug(s"Number of ${readerType} data predicates: ${numPredicates}")
      logger.debug(s"Number of ${readerType} data arguments: ${numArguments}")
    }

    val trainingFromSection = 2
    val trainingToSection = 2
    var gr: SRLMultiGraphDataModel = null
    if (!testOnly) {
      logger.info(s"Reading training data from sections ${trainingFromSection} to ${trainingToSection}")
      val trainReader = new SRLDataReader(
        rm.getString(SRLConfigurator.TREEBANK_HOME.key),
        rm.getString(SRLConfigurator.PROPBANK_HOME.key),
        trainingFromSection, trainingToSection
      )
      trainReader.readData()
      logger.info(s"Annotating ${trainReader.textAnnotations.size} training sentences")
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
    logger.info(s"Reading test data from section ${testSection}")
    testReader.readData()

    logger.info(s"Annotating ${testReader.textAnnotations.size} test sentences")
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
