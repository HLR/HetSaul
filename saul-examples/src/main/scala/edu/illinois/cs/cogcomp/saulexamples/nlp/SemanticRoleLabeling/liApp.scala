package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.ModelConfigs._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlConstraintClassifiers.argTypeConstraintClassifier
import org.slf4j.{Logger, LoggerFactory}
object liApp extends App {
  //train parameters
  val pipelineTrain= false
  val joinTrain= true
  //test parameters
  val pipeline= false
  val pipelineInTestA = false
  val pipelineInTestC = false
  val testWithConstraints = false


  //population parameters
  val useGoldPredicate = true
  val useGoldBoundaries = true

  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  val srlGraphs = populatemultiGraphwithSRLData(testOnly = false, useGoldPredicate, useGoldBoundaries)
  import srlGraphs._
  logger.info("all relations number after population:" + srlGraphs.relations().size)
  logger.info("all sentences number after population:" + srlGraphs.sentences().size)
  logger.info("all predicates number after population:" + srlGraphs.predicates().size)
  logger.info("all arguments number after population:" + srlGraphs.arguments().size)
  logger.info("all tokens number after population:" + srlGraphs.tokens().size)

  if (pipelineInTestA & !testWithConstraints){
     //load the argument identifier model,
      val modelLCb= bModelDir+argumentIdentifier_lc
      val modelLEXb= bModelDir+argumentIdentifier_lex
      argumentXuIdentifierGivenApredicate.load(modelLCb,modelLEXb)
      argumentXuIdentifierGivenApredicate.test()
      //argumentXuIdentifierGivenApredicate.test(prediction = isArgumentPrediction , groundTruth = isArgumentXuGold)
      //    filter the candidate arguments and then test the argument classifier (trained wiht GTh) on the filtered arguments.
      val modelLCa = aModelDir+argumentTypeLearner_lc
      val modelLEXa = aModelDir+argumentTypeLearner_lex
      argumentTypeLearner.load(modelLCa,modelLEXa)
      argumentTypeLearner.test(prediction = typeArgumentPipeGivenGoldPredicate, groundTruth = argumentLabelGold, exclude= "candidate")//grounexclude = "candidate")
  }

  if (pipelineInTestA && testWithConstraints) {

    //load the argument identifier model,
    val modelLCb= bModelDir+argumentIdentifier_lc
    val modelLEXb= bModelDir+argumentIdentifier_lex
    argumentXuIdentifierGivenApredicate.load(modelLCb,modelLEXb)
    argumentXuIdentifierGivenApredicate.test()
    //argumentXuIdentifierGivenApredicate.test(prediction = isArgumentPrediction , groundTruth = isArgumentXuGold)
    //    filter the candidate arguments and then test the argument classifier (trained wiht GTh) on the filtered arguments.
    val modelLCa = aModelDir+argumentTypeLearner_lc
    val modelLEXa = aModelDir+argumentTypeLearner_lex
    argumentTypeLearner.load(modelLCa,modelLEXa)
    argumentTypeLearner.test(prediction = typeArgumentPipeGivenGoldPredicateConstrained , groundTruth= argumentLabelGold, exclude= "candidate")//grounexclude = "candidate")
  }

  if (pipelineInTestC){
    //load the argument identifier model,
    val modelLCb= bModelDir+argumentIdentifier_lc
    val modelLEXb= bModelDir+argumentIdentifier_lex
    argumentXuIdentifierGivenApredicate.load(modelLCb,modelLEXb)
    argumentXuIdentifierGivenApredicate.test()
    //argumentXuIdentifierGivenApredicate.test(prediction = isArgumentPrediction , groundTruth = isArgumentXuGold)
    //    filter the candidate arguments and then test the argument classifier (trained wiht GTh) on the filtered arguments.
    val modelLCa = cModelDir+argumentTypeLearner_lc
    val modelLEXa = cModelDir+argumentTypeLearner_lex
    argumentTypeLearner.load(modelLCa,modelLEXa)
    argumentTypeLearner.test(prediction = typeArgumentPipeGivenGoldPredicate, groundTruth = argumentLabelGold, exclude= "candidate")//grounexclude = "candidate")
  }
  if (testWithConstraints && !pipeline){
    val modelLCc = cModelDir+argumentTypeLearner_lc
    val modelLEXc = cModelDir+argumentTypeLearner_lex
    argumentTypeLearner.load( modelLCc,modelLEXc)
    argTypeConstraintClassifier.test(outputGranularity= 100, exclude= "candidate")//grounexclude = "candidate")
  }

  if (joinTrain) {

    //argumentTypeLearner.load(aModelDir+argumentTypeLearner_lc, aModelDir+argumentTypeLearner_lex)
//    argumentTypeLearner.test()
//      import util.control.Breaks._
//      val goldOutFile = "srl.gold";
//      val goldWriter = new PrintWriter(new File(goldOutFile));
//      val predOutFile = "srl.predicted";
//      val predWriter = new PrintWriter(new File(predOutFile));
//      val writer = new ColumnFormatWriter();
//
//      val predictedViews = predArgViewGenerator.toPredArgList(srlGraphs, typeArgumentPrediction)
//      val goldViews = predArgViewGenerator.toPredArgList(srlGraphs, argumentLabelGold)
//      breakable {
//        predictedViews.zipWithIndex.foreach(
//          pav => {
//            writer.printPredicateArgumentView(pav._1, predWriter)
//            if (pav._2 == 593) {
//              println(pav._2)
//              break()
//            }
//          }
//        )
//      }
//      breakable {
//        goldViews.zipWithIndex.foreach(
//          pav =>
//            {
//              writer.printPredicateArgumentView(pav._1, goldWriter)
//              println(pav._2)
//              if (pav._2 == 593) {
//                println(pav._2)
//                break()
//              }
//            }
//        )
//      }
    logger.info("Join train:... ")
   // JointTrainSparseNetwork(srlGraphs, argTypeConstraintClassifier :: Nil, 3)
    argumentTypeLearner.setModelDir("models/joinModels_aTr/")
    argumentTypeLearner.learn()
    argumentTypeLearner.save()
    argumentTypeLearner.load(jModelDir+argumentTypeLearner_lc, jModelDir+argumentTypeLearner_lex)
    argumentTypeLearner.test()
    logger.info("join prediction:... ")
   // argTypeConstraintClassifier.test(srlGraphs.relations.getTestingInstances, (aModelDir + argumentTypeLearner_pred), 100, exclude = "candidate") //(aTr_pred, 100)
    //  predWriter.close()
    //  goldWriter.close()
  }

  //  arg_Is_TypeConstraintClassifier.test()

  // print("argument identifier L+I model (join with classifciation) test results:")

  // arg_IdentifyConstraintClassifier.test()

  // print("argument classifier L+I model (join with classifciation) test results:")

  // arg_Is_TypeConstraintClassifier.test()

  //print("argument classifier L+I model considering background knowledge  test results:")

if (pipelineTrain)
{
  val modelLCb= bModelDir+argumentIdentifier_lc
  val modelLEXb= bModelDir+argumentIdentifier_lex
  argumentXuIdentifierGivenApredicate.load(modelLCb,modelLEXb)
  val training= relations.getTrainingInstances.filter(x=> argumentXuIdentifierGivenApredicate(x).equals("true"))
  argumentTypeLearner.setModelDir("pipeModels_cTr")
  argumentTypeLearner.learn(100,training)
  argumentTypeLearner.test(exclude = "candidate")
  argumentTypeLearner.save()
  argumentTypeLearner.test(prediction = typeArgumentPipeGivenGoldPredicate, groundTruth = argumentLabelGold, exclude= "candidate")//grounexclude = "candidate")
}
}
