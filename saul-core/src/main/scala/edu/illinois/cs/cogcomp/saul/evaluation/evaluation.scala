package edu.illinois.cs.cogcomp.saul.evaluation

import edu.illinois.cs.cogcomp.saul.datamodel.node.Node
import edu.illinois.cs.cogcomp.saul.datamodel.property.Property

/** Created by Parisa on 1/4/16.
  */
object evaluation {

  def Test[T <: AnyRef](ground_truth: Property[T], actual: Property[T], ds: Node[T]): Unit = {
    val r1 = ds.getTestingInstances

    val results = r1.map({
      x =>
        val pri = actual(x)
        val truth = ground_truth(x)
        (pri, truth)
    })

    val allClasses = results.map(x => x._1).toList.distinct.union(results.map(x => x._2).toList.distinct).distinct

    allClasses.foreach {
      z =>
        val tp = results.count({ case (x, y) => x == y && (x == z) }) * 1.0
        val fp = results.count({ case (x, y) => x != y && (x == z) }) * 1.0

        val tn = results.count({ case (x, y) => x == y && (x != z) }) * 1.0
        val fn = results.count({ case (x, y) => x != y && (x != z) }) * 1.0
        println("\n-------------------------------------------------------")
        println(s" class: $z tp: $tp fp: $fp tn: $tn fn: $fn ")

        if (results.size > 0)
          println(s" accuracy    ${(tp + tn) / results.size} ")
        if ((tp + fp) > 0)
          println(s" precision   ${tp / (tp + fp)} ")
        else
          println(" precision 1")

        if ((tp + fn) > 0)
          println(s" recall      ${tp / (tp + fn)} ")
        else
          println(" recall  1")
        if (2 * tp + fp + fn > 0)
          println(s" f1          ${(2.0 * tp) / (2 * tp + fp + fn)} ")
        else
          println(" f1  1")
    }
  }

}
