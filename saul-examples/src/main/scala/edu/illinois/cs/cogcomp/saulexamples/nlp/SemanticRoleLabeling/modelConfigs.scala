package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

/**
 * Created by Parisa on 2/21/16.
 */
object ModelConfigs {

   val aModelDir: String = "../models/models_aTr/"
   val bModelDir: String = "../models/models_bTr/"
   val cModelDir: String = "../models/models_cTr/"
   val dModelDir: String = "../models/models_dTr/"
   val eModelDir: String = "../models/models_eTr/"
   val fModelDir: String = "../models/models_fTr/"

  val argumentTypeLearner_lc = "edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.argumentTypeLearner$.lc"
  val argumentTypeLearner_lex ="edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.argumentTypeLearner$.lex"
  val argumentTypeLearner_pred = "classifier-predictions.txt"
  val argumentIdentifier_lc= "edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.argumentXuIdentifierGivenApredicate$.lc"
  val argumentIdentifier_lex= "edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.argumentXuIdentifierGivenApredicate$.lex"
 // val predicateIdentifier_ls =
}
