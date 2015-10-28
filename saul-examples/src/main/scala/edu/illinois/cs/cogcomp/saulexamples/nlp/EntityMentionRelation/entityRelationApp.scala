package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRawToken, ConllRelation }
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.entityRelationClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.entityRelationDataModel._
import edu.illinois.cs.cogcomp.saul.classifier.JointTrain

/** Experiment workspace for playing with language feature. */

object myConfiguration {
  val iterations = 20
  val pipeLine = true
  val fold = 4
}

object entityRelationApp {

  def trainIndepedent(it: Int): Unit = {
    println("Indepent Training with iteration " + it)
    PersonClassifier.learn(it)
    PersonClassifier.test()

    orgClassifier.learn(it)
    orgClassifier.test()

    LocClassifier.learn(it)
    LocClassifier.test()

    workForClassifier.learn(it)
    workForClassifier.test()

    LivesInClassifier.learn(it)
    LivesInClassifier.test()

    //    locatedInClassifier.crossValidation(it)
    //    locatedInClassifier.test()
  }

  def cv(it: Int): Unit = {

    println("Running CV " + it)

    PersonClassifier.crossValidation(it)
    orgClassifier.crossValidation(it)
    LocClassifier.crossValidation(it)

    workForClassifier.crossValidation(it)
    LivesInClassifier.crossValidation(it)

  }

  def trainJoint(preIt: Int, it: Int): Unit = {
    println("Joint Training with Pretraint " + preIt)
    println("Joint Training with iteration " + it)
    orgClassifier.learn(8)
    PersonClassifier.learn(8)
    LocClassifier.learn(8)

    JointTrain.train[ConllRelation](entityRelationDataModel, PerConstraintClassifier :: orgConstraintClassifier :: LocConstraintClassifier :: P_O_relationClassifier :: LiveIn_P_O_relationClassifier :: Nil, it)
    //    JointTrain.train[ConllRelation](ErDataModelExample,  P_O_relationClassifier  :: LiveIn_P_O_relationClassifier ::Nil,it)
  }

  def forgotEverything() = {

    PersonClassifier.forget()
    orgClassifier.forget()
    //    PersonClassifier.forgot()
    workForClassifier.forget()
  }

  val pipeLine = myConfiguration.pipeLine

  def main(args: Array[String]) {

    val fold = myConfiguration.fold

    if (pipeLine) {
      println("using pipeline feature")
    }

    val it = myConfiguration.iterations

    forgotEverything()
    entityRelationDataModel.read(fold)

    val testRels = entityRelationDataModel.getNodeWithType[ConllRelation].getTestingInstances.toList //.map( ErDataModelExample.getFromRelation[ConllRelation,ConllRawToken](_)  ).flatten
    val testTokens = entityRelationDataModel.getNodeWithType[ConllRawToken].getTestingInstances.toList //.map( ErDataModelExample.getFromRelation[ConllRelation,ConllRawToken](_)  ).flatten

    trainIndepedent(it)

    println(Console.BLUE + "Peop")
    JointTrain.testClassifiers(PersonClassifier.classifier, (entityType is "Peop").classifier, testTokens)
    println(Console.RED + "Peop")
    JointTrain.testClassifiers(PerConstraintClassifier.classifier, (entityType is "Peop").classifier, testTokens)

    println(Console.BLUE + "Org")
    JointTrain.testClassifiers(orgClassifier.classifier, (entityType is "Org").classifier, testTokens)
    println(Console.RED + "Org")
    JointTrain.testClassifiers(orgConstraintClassifier.classifier, (entityType is "Org").classifier, testTokens)

    println(Console.BLUE + "Loc")
    JointTrain.testClassifiers(LocClassifier.classifier, (entityType is "Loc").classifier, testTokens)
    println(Console.RED + "Loc")
    JointTrain.testClassifiers(LocConstraintClassifier.classifier, (entityType is "Loc").classifier, testTokens)

    println(Console.BLUE + "Work_For")
    JointTrain.testClassifiers(workForClassifier.classifier, (relationType is "Work_For").classifier, testRels)
    println(Console.RED + "Work_For")
    JointTrain.testClassifiers(P_O_relationClassifier.classifier, (relationType is "Work_For").classifier, testRels)
    //

    println(Console.BLUE + "Live_In")
    JointTrain.testClassifiers(LivesInClassifier.classifier, (relationType is "Live_In").classifier, testRels)
    println(Console.RED + "Live_In")
    JointTrain.testClassifiers(LiveIn_P_O_relationClassifier.classifier, (relationType is "Live_In").classifier, testRels)
  }

  def moreExamples(): Unit = {

    //    println(Console.RED)
    //    println("Testing org")
    //    println(Console.RESET)
    //    orgClassifier.test()
    //    println(Console.GREEN)
    //    println("Testing constrainted org")
    //    println(Console.RESET)
    //    orgConstraintClassifier.test()
    //
    //
    //    println(Console.RED)
    //    println("Testing per")
    //    println(Console.RESET)
    //    PersonClassifier.test()
    //    println(Console.GREEN)
    //    println("Testing constrainted per")
    //    println(Console.RESET)
    //    PerConstraintClassifier.test()
    //
    //
    //    println(Console.RED)
    //    println("Testing works for")
    //    println(Console.RESET)
    //    workForClassifier.test()
    //    println(Console.GREEN)
    //    println("Testing constrainted works for")
    //    println(Console.RESET)
    //    P_O_relationClassifier.test()

    //  orgConstraintClassifier.crossValidation(5)

    //    workForClassifier.learn(1)
    //    workForClassifier.test()

    //    PersonClassifier.crossValidation(5)
    //    orgClassifier.crossValidation(5)
    //    LocClassifier.crossValidation(5)

    //    orgClassifier.test(Data.testData)

    //    println(Data.testData.ACCESS_RELATIONS)
    //
    //
    //    val rl = SomeExampleDataModel.getEntityWithType[ConllRelation].getAllInstances
    //    val r1 = rl.head
    //
    //    val t12 = SomeExampleDataModel.getFromRelation[ConllRelation,ConllRawToken](r1)
    //
    //
    //    val rf1 = SomeExampleDataModel.getFromRelation[ConllRawToken,ConllRelation](r1.e1)
    //    val rf2 = SomeExampleDataModel.getFromRelation[ConllRawToken,ConllRelation](r1.e2)
    //
    //    println(r1.e1.phrase)
    //    println(r1.e2.phrase)
    //    println("!!!!!!!")
    //    t12 foreach (x => println(x.phrase))
    //    println("@@@@@@@")
    //    rf1 foreach (x => println(x.e1 + "~~" + x.e2 ))
    //    println("#######")
    //    rf2 foreach (x => println(x.e1 + "~~" + x.e2 ))
    //
    //    val tokens =  SomeExampleDataModel.tokens
    //
    //    val joined = tokens join tokens on
    //                 'sid === 'sid filter
    //                 { case (t1,t2) => t1.wordId != t2.wordId }
    //
    ////    joined.toE
    //
    //
    //    tokens.getAllInstances filter (_.sentId == 271) foreach println
    //    println("#######")
    //    joined filter (_._1.sentId == 271) foreach println
    //
    //    println("#######")
    //    val reader = new Conll04_RelationReaderNew("./data/conll04_train.corp", "Token")
    //    reader.instances.toList.filter(_.sentId == 271) foreach println
    //
    //    //  println(p2)
    //
    //    JointTrain(SomeExampleDataModel, List(PerConstraintClassifier,orgConstraintClassifier,P_O_relationClassifier))
    //

    /// Parisa's verison
    //=======
    //
    //    orgClassifier.learn(10)
    //    orgClassifier.test(Data.testData)
    //
    //    println(Data.testData.ACCESS_RELATIONS)
    //
    //
    //    val rl = SomeExampleDataModel.getEntityWithType[ConllRelation].getAllInstances
    //    val r1 = rl.head
    //
    //    val t12 = SomeExampleDataModel.getFromRelation[ConllRelation,ConllRawToken](r1)
    //
    //
    //    val rf1 = SomeExampleDataModel.getFromRelation[ConllRawToken,ConllRelation](r1.e1)
    //    val rf2 = SomeExampleDataModel.getFromRelation[ConllRawToken,ConllRelation](r1.e2)
    //
    //    println(r1.e1.phrase)
    //    println(r1.e2.phrase)
    //    println("!!!!!!!")
    //    t12 foreach (x => println(x.phrase))
    //    println("@@@@@@@")
    //    rf1 foreach (x => println(x.e1 + "~~" + x.e2 ))
    //    println("#######")
    //    rf2 foreach (x => println(x.e1 + "~~" + x.e2 ))
    //
    //    val tokens =  SomeExampleDataModel.tokens
    //
    //    val joined = tokens join tokens on
    //                 'sid === 'sid filter
    //                 { case (t1,t2) => t1.wordId != t2.wordId }
    //
    ////    joined.toE
    //
    //
    //    tokens.getAllInstances filter (_.sentId == 271) foreach println
    //    println("#######")
    //    joined filter (_._1.sentId == 271) foreach println
    //
    //    println("#######")
    //    val reader = new Conll04_RelationReaderNew("./data/conll04_train.corp", "Token")
    //    reader.instances.toList.filter(_.sentId == 271) foreach println
    //
    //    //  println(p2)
    //
    //    JointTrain(SomeExampleDataModel, List(PerConstraintClassifier,orgConstraintClassifier,P_O_relationClassifier))

  }
}