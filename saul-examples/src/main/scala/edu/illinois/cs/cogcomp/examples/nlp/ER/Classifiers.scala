package edu.illinois.cs.cogcomp.examples.nlp.ER

//import com.example.{MyConfiguration, RunnableExample}

import edu.illinois.cs.cogcomp.example.er_task.datastruct.{ConllRawSentence, ConllRawToken, ConllRelation}
import edu.illinois.cs.cogcomp.examples.firstexamples.Constrains._
import edu.illinois.cs.cogcomp.lfs.classifier.{ConstraintClassifier, Learnable}
import edu.illinois.cs.cogcomp.lfs.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.lfs.data_model.DataModel
import edu.illinois.cs.cogcomp.lfs.data_model.attribute.Attribute

//import example.er_task.datastruct._


/**
 * Created by haowu on 1/27/15.
 */
object Classifiers {

  import edu.illinois.cs.cogcomp.examples.firstexamples.ErDataModelExample._
// TODO : Write the type conversion
//  val orgFeature = List(pos,entityType)


  object orgClassifier extends Learnable[ConllRawToken](ErDataModelExample) {

    def label: Attribute[ConllRawToken] = entityType is "Org"
    //override def feature = using(
     //word,phrase,containsSubPhraseMent,containsSubPhraseIng,
     // containsInPersonList,wordLen,containsInCityList
   // )
  }


  object PersonClassifier extends Learnable[ConllRawToken](ErDataModelExample){



    def label : Attribute[ConllRawToken]  = entityType is "Peop"
    override def feature = using(
      windowWithIn[ConllRawSentence](-2,2, ~~(
        pos
      )),word,phrase,containsSubPhraseMent,containsSubPhraseIng,
      containsInPersonList,wordLen,containsInCityList
    )

  }

  object LocClassifier extends Learnable[ConllRawToken](ErDataModelExample){



    def label : Attribute[ConllRawToken]  = entityType is "Loc"
    override def feature = using(
      windowWithIn[ConllRawSentence](-2,2, ~~(
        pos
      )),word,phrase,containsSubPhraseMent,containsSubPhraseIng,
      containsInPersonList,wordLen,containsInCityList
    )

  }

  val ePipe = DataModel.discreteAttributesGeneratorOf[ConllRelation]('e1pipe){
    rel => {
        "e1-org: " + orgClassifier.discreteValue(rel.e1) ::
        "e1-per: " + PersonClassifier.discreteValue(rel.e1) ::
        "e1-loc: " + LocClassifier.discreteValue(rel.e1) ::
        "e2-org: " + orgClassifier.discreteValue(rel.e1) ::
        "e2-per: " + PersonClassifier.discreteValue(rel.e1) ::
        "e2-loc: " + LocClassifier.discreteValue(rel.e1) ::
      Nil
    }
  }

  object workForClassifier extends Learnable[ConllRelation](ErDataModelExample) {
    override def label : Attribute[ConllRelation] = relationType is "Work_For"

    override def feature =  using(
      relFeature,relPos//,ePipe
    )
  }


  object workForClassifierPipe extends Learnable[ConllRelation](ErDataModelExample) {
    override def label : Attribute[ConllRelation] = relationType is "Work_For"

    override def feature =  using(
      relFeature,relPos,ePipe
    )
  }

  object LivesInClassifier extends Learnable[ConllRelation](ErDataModelExample) {
    override def label : Attribute[ConllRelation] = relationType is "Live_In"
//    val pipeLineFeature =
    override def feature = using(
      relFeature,relPos//,ePipe
    )
  }

  object LivesInClassifierPipe extends Learnable[ConllRelation](ErDataModelExample) {
    override def label : Attribute[ConllRelation] = relationType is "Live_In"
    //    val pipeLineFeature =
    override def feature = using(
      relFeature,relPos,ePipe
    )
  }



  object org_baseClassifier extends Learnable[ConllRelation](ErDataModelExample) {
    override def label : Attribute[ConllRelation] = relationType is "OrgBased_In"
  }
  object locatedInClassifier extends Learnable[ConllRelation](ErDataModelExample) {
    override def label : Attribute[ConllRelation] = relationType is "Located_In"
  }

object orgConstraintClassifier extends ConstraintClassifier[ConllRawToken,ConllRelation](ErDataModelExample, orgClassifier){
   def subjectTo = Per_Org
   override val pathToHead = Some('containE2)
   override def filter(t: ConllRawToken,h:ConllRelation): Boolean = (t.wordId==h.wordId1)
}
   

  object PerConstraintClassifier extends ConstraintClassifier[ConllRawToken,ConllRelation](ErDataModelExample, PersonClassifier){

    def subjectTo = Per_Org
    override val pathToHead=Some('containE1)
    override def filter(t: ConllRawToken,h:ConllRelation): Boolean = t.wordId==h.wordId2
  }


  object LocConstraintClassifier extends ConstraintClassifier[ConllRawToken,ConllRelation](ErDataModelExample, LocClassifier){

    def subjectTo = Per_Org
    override val pathToHead=Some('containE2)
    //    override def filter(t: ConllRawToken,h:ConllRelation): Boolean = t.wordId==h.wordId2
  }



  object P_O_relationClassifier extends ConstraintClassifier[ConllRelation,ConllRelation](ErDataModelExample,workForClassifier){
    def subjectTo= Per_Org
  }


  object LiveIn_P_O_relationClassifier extends ConstraintClassifier[ConllRelation,ConllRelation](ErDataModelExample,LivesInClassifier){
    def subjectTo= Per_Org
  }
}

