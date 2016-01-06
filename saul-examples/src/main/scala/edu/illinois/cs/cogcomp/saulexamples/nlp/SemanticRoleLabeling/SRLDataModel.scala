package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{Constituent, Relation, TextAnnotation}
import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree
import edu.illinois.cs.cogcomp.edison.features.FeatureUtilities
import edu.illinois.cs.cogcomp.edison.features.factory._
import edu.illinois.cs.cogcomp.nlp.corpusreaders.CoNLLColumnFormatReader
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiersForExperiment.{argumentTypeLearner1, argumentXuIdentifierGivenApredicate1, predicateClassifier1}
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLSensors._

import scala.collection.JavaConversions._

/** Created by Parisa on 12/23/15.
  */
object SRLDataModel extends DataModel {
  val predicates = node[Constituent]

  val arguments = node[Constituent]

  val relations = node[Relation]

  //val onTheFlyRelationNode= join(predicates, arguments)

  val sentences = node[TextAnnotation]

  val trees = node[Tree[Constituent]]

  val stringTree = node[Tree[String]]

  val tokens = node[Constituent]

  // val sentencesToTrees = edge(sentences, trees)
  val sentencesTostringTree = edge(sentences, stringTree)
  val sentencesToTokens = edge(sentences, tokens)
  val sentencesToRelations = edge(sentences, relations)
  val relationsToPredicates = edge(relations, predicates)
  val relationsToArguments = edge(relations, arguments)

  //TODO PARSE_GOLD is only good for training; for testing we need PARSE_STANFORD or PARSE_CHARNIAK

  sentencesToRelations.addSensor(textAnnotationToRelation _)
  sentencesToRelations.addSensor(textAnnotationToRelationMatch _)
  //  sentencesToTrees.addSensor(SRLSensors.textAnnotationToTree _)
  relationsToArguments.addSensor(relToArgument _)
  relationsToPredicates.addSensor(relToPredicate _)
  sentencesTostringTree.addSensor(textAnnotationToStringTree _)

  val isPredicate_Gth = property[Constituent]("p") {
    x: Constituent => x.getLabel.equals("Predicate")
  }
  val predicateSense_Gth = property[Constituent]("s") {
    x: Constituent => x.getAttribute(CoNLLColumnFormatReader.SenseIdentifer)
  }

  val isArgument_Gth = property[Constituent]("a") {
    x: Constituent => x.getLabel.equals("Argument")
  }
  val isArgumentXu_Gth = property[Relation]("aX") {
    x: Relation => !x.getRelationName.equals("candidate")
  }
  val argumentLabel_Gth = property[Relation]("l") {
    r: Relation => r.getRelationName
  }

  val posTag = property[Constituent]("posC") {
    x: Constituent => x.getTextAnnotation.getView(ViewNames.POS).getConstituentsCovering(x).get(0).getLabel
  }

  val address = property[Constituent]("add") {
    x: Constituent => x.getTextAnnotation.getCorpusId + ":" + x.getTextAnnotation.getId + ":" + x.getSpan
  }
  val subcategorization = property[Constituent]("subcatC") {
    x: Constituent => //new SubcategorizationFrame("Charniak").getFeatures(x)
      val subcatFex = new SubcategorizationFrame(ViewNames.PARSE_GOLD)
      val discreteFeature: String = FeatureUtilities.getFeatureSet(subcatFex, x).mkString
      discreteFeature
  }

  val phraseType = property[Constituent]("phraseTypeC") {
    x: Constituent =>
      val phraseType = new ParsePhraseType(ViewNames.PARSE_GOLD)
      val discreteFeature: String = FeatureUtilities.getFeatureSet(phraseType, x).mkString
      discreteFeature
  }

  val headword = property[Constituent]("headC") {
    x: Constituent =>
      val headWordAndPos = new ParseHeadWordPOS(ViewNames.PARSE_GOLD)
      val discreteFeature: String = FeatureUtilities.getFeatureSet(headWordAndPos, x).mkString
      discreteFeature
  }

  val syntacticFrame = property[Constituent]("synFrameC") {
    x: Constituent =>
      val syntacticFrame = new SyntacticFrame(ViewNames.PARSE_GOLD)
      val discreteFeature: String = FeatureUtilities.getFeatureSet(syntacticFrame, x).mkString
      discreteFeature
  }
  val path = property[Constituent]("pathC") {
    x: Constituent =>
      val parspath = new ParsePath(ViewNames.PARSE_GOLD)
      val discreteFeature: String = FeatureUtilities.getFeatureSet(parspath, x).mkString
      discreteFeature
  }

  val subcategorizationRelation = property[Relation]("subcat") {
    x: Relation => //new SubcategorizationFrame("Charniak").getFeatures(x)
      val subcatFex = new SubcategorizationFrame(ViewNames.PARSE_GOLD)
      val discreteFeature: String = FeatureUtilities.getFeatureSet(subcatFex, x.getTarget).mkString
      discreteFeature
  }

  val phraseTypeRelation = property[Relation]("phraseType") {
    x: Relation =>
      val phraseType = new ParsePhraseType(ViewNames.PARSE_GOLD)
      val discreteFeature: String = FeatureUtilities.getFeatureSet(phraseType, x.getTarget).mkString
      discreteFeature
  }

  val headwordRelation = property[Relation]("head") {
    x: Relation =>

      val headWordAndPos = new ParseHeadWordPOS(ViewNames.PARSE_GOLD)
      val discreteFeature: String = FeatureUtilities.getFeatureSet(headWordAndPos, x.getTarget).mkString
      discreteFeature
  }

  val syntacticFrameRelation = property[Relation]("synFrame") {
    x: Relation =>
      val syntacticFrame = new SyntacticFrame(ViewNames.PARSE_GOLD)
      val discreteFeature: String = FeatureUtilities.getFeatureSet(syntacticFrame, x.getTarget).mkString
      discreteFeature
  }
  val pathRelation = property[Relation]("path") {
    x: Relation =>
      val parspath = new ParsePath(ViewNames.PARSE_GOLD)
      val discreteFeature: String = FeatureUtilities.getFeatureSet(parspath, x.getTarget).mkString
      discreteFeature
  }
  val predPosTag = property[Relation]("pPos") {
    x: Relation => x.getSource.getTextAnnotation.getView(ViewNames.POS).getConstituentsCovering(x.getSource).get(0).getLabel
  }
  val predLemma = property[Relation]("pLem") {
    x: Relation =>
      val l = x.getSource.getTextAnnotation.getView(ViewNames.LEMMA).getConstituentsCovering(x.getSource).get(0).getLabel
      l
  }
  val linearPosition = property[Relation]("position") {
    x: Relation =>
      val linposition = new LinearPosition()
      val discreteFeature: String = FeatureUtilities.getFeatureSet(linposition, x.getTarget).mkString
      discreteFeature
  }
//Classifiers as properties

    val isPredicatePrediction = property[Constituent]("isPredicatePrediction") {
      x: Constituent => predicateClassifier1(x)
    }

    val isArgumentPrediction = property[Relation]("isArgumentPrediction") {
      x: Relation => argumentXuIdentifierGivenApredicate1(x)
    }

    val isArgumentPipePrediction = property[Relation]("isArgumentpipPrediction") {
      x: Relation =>
        predicateClassifier1(x.getSource) match {
          case "false" => "false"
          case _ => argumentXuIdentifierGivenApredicate1(x)

        }
    }
   val typeArgumentPrediction = property[Relation]("typeArgumentPrediction"){
     x: Relation =>
       argumentTypeLearner1(x)
   }
    val typeArgumentPipePrediction = property[Relation]("typeArgumentpipPrediction") {
          x: Relation =>
            val a:String= predicateClassifier1(x.getSource) match {
              case "false" => "false"
              case _ => argumentXuIdentifierGivenApredicate1(x)}
            val b= a match {
              case "false" => "false"
              case _ => argumentTypeLearner1(x)
            }
            b
        }
    }
