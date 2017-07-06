/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import java.io._
import edu.illinois.cs.cogcomp.core.datastructures.IntPair
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Eval._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2013.{ RELATION, SpRL2013Document }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Triplet.{ SpRLLabels, SpRLRelation }

import scala.collection.JavaConverters._
import scala.collection.immutable.HashSet
import scala.reflect.io.File

/** Created by Parisa on 7/29/16.
  */
object SpRLApp extends App with Logging {
  import SpRLConfigurator._

  val properties: ResourceManager = {
    logger.info("Loading default configuration parameters")
    new SpRLConfigurator().getDefaultConfig
  }
  val modelDir = properties.getString(MODELS_DIR) +
    File.separator + properties.getString(SpRL_MODEL_DIR) + File.separator
  val isTrain = properties.getBoolean(IS_TRAINING)
  val version = properties.getString(VERSION)
  var modelName = properties.getString(MODEL_NAME)

  logger.info("population starts.")

  modelName match {
    case name if name == "Triplet" =>
      populateData(name)
      if (isTrain) {
        trainRelationClassifier(SpRLClassifiers.relationClassifier, name)
        trainRelationClassifier(SpRLClassifiers.spatialIndicatorClassifier, name + "/sp")
        trainRelationClassifier(SpRLClassifiers.trajectorClassifier, name + "/tr")
        trainRelationClassifier(SpRLClassifiers.landmarkClassifier, name + "/lm")
      } else {

        val relations = testRelationClassifier(SpRLClassifiers.relationClassifier, name,
          _.getRelationLabel.toString, _.getRelationEval)

        val sp = testRelationClassifier(
          SpRLClassifiers.spatialIndicatorClassifier, name + "/sp",
          _.getSpLabel.equals(SpRLLabels.GOLD).toString, getSpatialIndicatorEval
        ).groupBy(x => new IntPair(x.getStart, x.getEnd)).map(_._2.head).toList

        val tr = testRelationClassifier(
          SpRLClassifiers.trajectorClassifier, name + "/tr",
          _.getTrLabel.equals(SpRLLabels.GOLD).toString, getTrajectorEval
        ).groupBy(x => new IntPair(x.getStart, x.getEnd)).map(_._2.head).toList

        val lm = testRelationClassifier(
          SpRLClassifiers.landmarkClassifier, name + "/lm",
          _.getLmLabel.equals(SpRLLabels.GOLD).toString, getLandmarkEval
        ).groupBy(x => new IntPair(x.getStart, x.getEnd)).map(_._2.head).toList

        val evaluator = new SpRLEvaluator()

        val predictedRelations = new RelationsEvalDocument(relations.asJava)
        val actualRelations = getRelationsEvalDocumentFromSpRL2013Corpus(getDataPath())
        val relationResults = evaluator.evaluateRelations(actualRelations, predictedRelations)

        val predictedRoles = new RolesEvalDocument(tr.asJava, sp.asJava, lm.asJava)
        val actualRoles = getRolesEvalDocumentFromSpRL2013Corpus(getDataPath())
        val roleResults = evaluator.evaluateRoles(actualRoles, predictedRoles)

        val relationTr = relations.map(x => new RoleEval(x.getTrajectorStart, x.getTrajectorEnd))
          .groupBy(x => new IntPair(x.getStart, x.getEnd)).map(_._2.head).toList.asJava

        val relationSp = relations.map(x => new RoleEval(x.getSpatialIndicatorStart, x.getSpatialIndicatorEnd))
          .groupBy(x => new IntPair(x.getStart, x.getEnd)).map(_._2.head).toList.asJava

        val relationLm = relations.map(x => new RoleEval(x.getLandmarkStart, x.getLandmarkEnd)).filter(x => x.getStart >= 0)
          .groupBy(x => new IntPair(x.getStart, x.getEnd)).map(_._2.head).toList.asJava

        val predictedRolesFromRelations = new RolesEvalDocument(relationTr, relationSp, relationLm)
        val rolesFromRelationsResults = evaluator.evaluateRoles(actualRoles, predictedRolesFromRelations)

        relationResults.addAll(rolesFromRelationsResults)
        SpRLEvaluator.printEvaluation(relationResults)
        SpRLEvaluator.printEvaluation(roleResults)

      }
  }

  def getRolesEvalDocumentFromSpRL2013Corpus(path: String): RolesEvalDocument = {
    val reader = new SpRLDataReader(path, classOf[SpRL2013Document])
    reader.readData()
    val tr = reader.documents.asScala
      .flatMap(_.getTAGS.getTRAJECTOR.asScala.map(x => new RoleEval(x.getStart.intValue(), x.getEnd.intValue())))
      .filter(x => x.getStart >= 0).toList

    val lm = reader.documents.asScala
      .flatMap(_.getTAGS.getLANDMARK.asScala.map(x => new RoleEval(x.getStart.intValue(), x.getEnd.intValue())))
      .filter(x => x.getStart >= 0).toList

    val sp = reader.documents.asScala
      .flatMap(_.getTAGS.getSPATIALINDICATOR.asScala.map(x => new RoleEval(x.getStart.intValue(), x.getEnd.intValue())))
      .filter(x => x.getStart >= 0).toList
    new RolesEvalDocument(tr.asJava, sp.asJava, lm.asJava)
  }

  def getRelationsEvalDocumentFromSpRL2013Corpus(path: String): RelationsEvalDocument = {
    val reader = new SpRLDataReader(path, classOf[SpRL2013Document])
    reader.readData()
    val relations = reader.documents.asScala
      .flatMap(doc => doc.getTAGS.getRELATION.asScala.map(x => getRelationEval(doc, x)))
      .toList

    new RelationsEvalDocument(relations.asJava)
  }

  def populateData(name: String): Unit = {
    val lexPath = modelDir + version + File.separator + name + File.separator + "lexicon.lex"
    val lex = if (isTrain) null else loadSpLexicon(lexPath)
    PopulateSpRLDataModel(getDataPath, isTrain, version, modelName, lex)
    if (isTrain) {
      saveSpLexicon(lexPath)
    }
  }

  def trainRelationClassifier(classifier: Learnable[SpRLRelation], name: String): Unit = {

    classifier.modelDir = modelDir + version + File.separator + name + File.separator
    logger.info("training " + name + "...")
    classifier.learn(100)
    classifier.save()
    logger.info("done.")
  }

  def testRelationClassifier[T <: AnyRef](classifier: Learnable[SpRLRelation], name: String,
    getLabel: (SpRLRelation) => String,
    getXmlObj: (SpRLRelation) => T): List[T] = {

    classifier.modelDir = modelDir + version + File.separator + name + File.separator
    classifier.load()
    logger.info("testing " + name + " ...")
    val xmlList = SpRLDataModel.relations.getTestingInstances.map(x => {
      classifier(x) match {
        case "GOLD" | "true" => getXmlObj(x)
        case _ => None.asInstanceOf[T]
      }
    }).filter(x => x != None).toList
    logger.info("done.")
    xmlList
  }

  def loadSpLexicon(filePath: String): HashSet[String] = {
    if (File(filePath).exists) {
      val stream = new ObjectInputStream(new FileInputStream(filePath))
      val lex = stream.readObject().asInstanceOf[HashSet[String]]
      stream.close()
      return lex
    }
    return null
  }

  def saveSpLexicon(filePath: String): Unit = {
    if (!File(filePath).parent.exists) {
      File(filePath).parent.createDirectory()
    }
    val stream = new ObjectOutputStream(new FileOutputStream(filePath))
    stream.writeObject(Dictionaries.spLexicon)
    stream.close()
  }

  def getDataPath(): String = {
    if (isTrain) properties.getString(TRAIN_DIR)
    else properties.getString(TEST_DIR)
  }

  def getSpatialIndicatorEval(r: SpRLRelation): RoleEval = {
    val offset: Int = r.getSentenceOffset.getFirst
    val spStart: Int = r.getSpatialIndicator.getFirstConstituent.getStartCharOffset + offset
    val spEnd: Int = r.getSpatialIndicator.getFirstConstituent.getEndCharOffset + offset
    new RoleEval(spStart, spEnd)
  }

  def getLandmarkEval(r: SpRLRelation): RoleEval = {
    val offset: Int = r.getSentenceOffset.getFirst
    val lmStart: Int = r.landmarkIsDefined match {
      case true => r.getLandmark.getFirstConstituent.getStartCharOffset + offset
      case _ => -1
    }
    val lmEnd: Int = r.landmarkIsDefined match {
      case true => r.getLandmark.getFirstConstituent.getEndCharOffset + offset
      case _ => -1
    }
    new RoleEval(lmStart, lmEnd)
  }

  def getTrajectorEval(r: SpRLRelation): RoleEval = {
    val offset: Int = r.getSentenceOffset.getFirst
    val trStart: Int = r.getTrajector.getFirstConstituent.getStartCharOffset + offset
    val trEnd: Int = r.getTrajector.getFirstConstituent.getEndCharOffset + offset
    new RoleEval(trStart, trEnd)
  }

  def getRelationEval(doc: SpRL2013Document, r: RELATION): RelationEval = {
    new RelationEval(
      doc.getTrajectorHashMap.get(r.getTrajectorId).getStart.intValue,
      doc.getTrajectorHashMap.get(r.getTrajectorId).getEnd.intValue,
      doc.getSpatialIndicatorMap.get(r.getSpatialIndicatorId).getStart.intValue,
      doc.getSpatialIndicatorMap.get(r.getSpatialIndicatorId).getEnd.intValue,
      doc.getLandmarkHashMap.get(r.getLandmarkId).getStart.intValue,
      doc.getLandmarkHashMap.get(r.getLandmarkId).getEnd.intValue
    )
  }
}
