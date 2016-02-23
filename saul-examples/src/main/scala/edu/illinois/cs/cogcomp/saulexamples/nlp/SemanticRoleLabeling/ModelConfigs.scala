package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.saulexamples.ExamplesConfigurator

/** Created by Parisa on 2/21/16.
  */
object ModelConfigs {
  val rootModelDir = new ExamplesConfigurator().getDefaultConfig.getString(ExamplesConfigurator.MODELS_DIR)

  val aModelDir: String = rootModelDir + "models_aTr/"
  val bModelDir: String = rootModelDir + "models_bTr/"
  val cModelDir: String = rootModelDir + "models_cTr/"
  val dModelDir: String = rootModelDir + "models_dTr/"
  val eModelDir: String = rootModelDir + "models_eTr/"
  val fModelDir: String = rootModelDir + "models_fTr/"

  val argumentTypeLearner_lc = "edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.argumentTypeLearner$.lc"
  val argumentTypeLearner_lex = "edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.argumentTypeLearner$.lex"
  val argumentTypeLearner_pred = "classifier-predictions.txt"
  val argumentIdentifier_lc = "edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.argumentXuIdentifierGivenApredicate$.lc"
  val argumentIdentifier_lex = "edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.argumentXuIdentifierGivenApredicate$.lex"
  // val predicateIdentifier_ls =
}
