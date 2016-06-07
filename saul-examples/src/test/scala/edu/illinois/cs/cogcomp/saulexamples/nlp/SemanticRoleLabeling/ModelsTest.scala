package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.saul.classifier.ClassifierUtils
import edu.illinois.cs.cogcomp.saulexamples.data.SRLFrameManager
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiers._
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions._
/**
 * Created by Parisa on 6/6/16.
 */
object ModelsTest extends App {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  logger.isInfoEnabled
  val rm = new SRLConfigurator().getConfig(Map("treebankHome" -> "./saul-examples/src/main/resources/srlToy/treebank","propbankHome"->"./saul-examples/src/main/resources/srlToy/propbank", "testSection"->"00"))
  val frameManager: SRLFrameManager = new SRLFrameManager(rm.getString(SRLConfigurator.PROPBANK_HOME.key))
  val parseViewName = rm.getString(SRLConfigurator.SRL_PARSE_VIEW)
  val SRLDataModel = new SRLMultiGraphDataModel(parseViewName, frameManager)
  val srlGraphs = PopulateSRLDataModel(true,true,true,rm)
  logger.info("all relations number after population:" + srlGraphs.relations().size)
  logger.info("all sentences number after population:" + srlGraphs.sentences().size)
  logger.info("all predicates number after population:" + srlGraphs.predicates().size)
  logger.info("all arguments number after population:" + srlGraphs.arguments().size)
  logger.info("all tokens number after population:" + srlGraphs.tokens().size)

  ClassifierUtils.LoadClassifier(SRLConfigurator.SRL_JAR_MODEL_PATH.value + "/models_aTr", argumentTypeLearner)
  argumentTypeLearner.test(exclude = "candidate")
}
