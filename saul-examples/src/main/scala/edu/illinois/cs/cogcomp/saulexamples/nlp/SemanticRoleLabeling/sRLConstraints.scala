package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Relation, TextAnnotation }
import edu.illinois.cs.cogcomp.lbjava.infer.{ FirstOrderConstant, FirstOrderConstraint }
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiers.argumentTypeLearner
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.relationAppXuPalmerCandidates._

/** Created by Parisa on 12/23/15.
  */
object sRLConstraints {
  val noOverlap = ConstrainedClassifier.constraintOf[TextAnnotation]( // here tis constituent is a sentence

    {
      var a: FirstOrderConstraint = new FirstOrderConstant(true)
      x: TextAnnotation => {
        (sentences(x) ~> sentencesToRelations ~> relationsToPredicates).foreach {
          y =>
            {
              a = Xucandisates(y).toList._atMost(1)({
                (p: Relation) =>
                  (argumentTypeLearner on p isTrue)
              })
            }
        }
        // on the global object TextAnnoatation
      }
      a
    }
  )

  // atmost 1 of (Argument a in containsWord)
  // 16. ArgumentTypeLearner(a) !: "null

  //(int i = 0; i < sentence.verbCount(); ++i)
  ////      3. ParseTreeWord verb = sentence.getVerb(i);
  //      4. LinkedList forVerb = sentence.getCandidates(verb);
  //      5. for (int j = 0; j < sentence.words.length; ++j) {
  //        6. if (sentence.words[j] == verb) continue;
  //        7. LinkedList containsWord = new LinkedList();
  //        8. for (Iterator I = forVerb.iterator(); I.hasNext(); ) {
  //          9. Argument candidate = (Argument) I.next();
  //          10. ParseTreeNode constituent = candidate.getConstituent();
  //          11. if (constituent.firstWordIndex() <= sentence.words[j].index
  //            12. && sentence.words[j].index <= constituent.lastWordIndex())
  //          13. containsWord.add(candidate);
  //          14. }
  //        15. atmost 1 of (Argument a in containsWord)
  //        16. ArgumentTypeLearner(a) !: "null";
  //        17. }
  //      18. }
  //    19. }

}

