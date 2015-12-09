package edu.illinois.cs.cogcomp.saul.classifier.trainingParadigms

import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.datamodel.node.NodeSet

import scala.reflect.ClassTag

object independent_train {
  def apply(c: (Learnable[_],NodeSet[_])*)(implicit t: ClassTag[_]) ={
   c.foreach(x => (x._1.learn(10, x._2)))
}

object independent_test {
  def apply(c: (Learnable[_], NodeSet[_])*) = {

    c.foreach
    { case x: (Learnable[_],NodeSet[_]) =>{
      type F=x._2.n
     // val typedC= x._2.asInstanceOf[NodeSet[_]]

      type D=x._1.n
      //val typedD= x._1.test(typedC)
      //typedD.test(typedC)

    }
    }
  }
}

object forgetAll {
  def apply(c: Learnable[_]*): Unit = {
    c.foreach((x: Learnable[_]) => x.forget())
  }
}