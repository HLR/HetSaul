package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.saul.classifier.ClassifierUtils
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiers._
import org.scalatest.{ FlatSpec, Matchers }

/** Created by Parisa on 6/6/16.
  */

class ModelsTest extends FlatSpec with Matchers {

  "argument type classifier (aTr)" should "work." in {
    ClassifierUtils.LoadClassifier(SRLConfigurator.SRL_JAR_MODEL_PATH.value + "/models_aTr/", argumentTypeLearner)
    val scores = argumentTypeLearner.test(exclude = "candidate")
    scores.foreach {
      case (label, score) => {
        label match {
          case "A0" => (score._1 >= 0.9) should be(true)
          case "A1" => (score._1 >= 0.9) should be(true)
          case "A2" => (score._1 >= 0.6) should be(true)
          case _ => (score._1 >= 0.0) should be(true)
        }
      }
    }
  }

  "predicate identifier (dTr)" should "perform higher than 0.98." in {
    ClassifierUtils.LoadClassifier(SRLConfigurator.SRL_JAR_MODEL_PATH.value + "/models_dTr/", predicateClassifier)
    val scores = predicateClassifier.test()
    scores.foreach {
      case (label, score) =>
        label match { case "true" => (score._1 >= 0.98) should be(true) }
    }
  }

  "L+I argument type classifier (aTr)" should "work." in {
    //Todo solve the test problem with Gurobi licencing vs. OJalgoHook inefficiency
    //    ClassifierUtils.LoadClassifier(SRLConfigurator.SRL_JAR_MODEL_PATH.value + "/models_aTr/", argumentTypeLearner)
    //    val scores = argTypeConstraintClassifier.test(exclude = "candidate")
    //    scores.foreach {
    //      case (label, score) => {
    //        label match {
    //          case "A0" => (score._1 >= 0.9) should be(true)
    //          case "A1" => (score._1 >= 0.9) should be(true)
    //          case "A2" => (score._1 >= 0.6) should be(true)
    //          case _ => ""
    //        }
    //      }
    //    }
  }
  "argument identifier (bTr)" should "perform higher than 0.95." in {
    ClassifierUtils.LoadClassifier(SRLConfigurator.SRL_JAR_MODEL_PATH.value + "/models_bTr/", argumentXuIdentifierGivenApredicate)
    val scores = argumentXuIdentifierGivenApredicate.test()
    scores.foreach {
      case (label, score) =>
        label match { case "true" => (score._1 >= 0.95) should be(true) }
    }
  }
  "argument identifier (cTr) trained with XuPalmer" should "perform higher than 0.9." in {
    ClassifierUtils.LoadClassifier(SRLConfigurator.SRL_JAR_MODEL_PATH.value + "/models_cTr/", argumentTypeLearner)
    val scores = argumentTypeLearner.test()
    scores.foreach {
      case (label, score) => {
        label match {
          case "A0" => (score._1 >= 0.9) should be(true)
          case "A1" => (score._1 >= 0.9) should be(true)
          case "A2" => (score._1 >= 0.6) should be(true)
          case _ => ""
        }
      }

    }
  }

  "argument identifier (fTr) trained with XuPalmer and candidate predicates" should "work." in {
    ClassifierUtils.LoadClassifier(SRLConfigurator.SRL_JAR_MODEL_PATH.value + "/models_fTr/", argumentTypeLearner)
    val scores = argumentTypeLearner.test(exclude = "candidate")
    scores.foreach {
      case (label, score) => {
        label match {
          case "A0" => (score._1 >= 0.9) should be(true)
          case "A1" => (score._1 >= 0.9) should be(true)
          case "A2" => (score._1 >= 0.6) should be(true)
          case _ => ""
        }
      }
    }
  }
}

