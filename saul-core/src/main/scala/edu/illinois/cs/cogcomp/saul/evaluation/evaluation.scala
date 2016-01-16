package edu.illinois.cs.cogcomp.saul.evaluation

import edu.illinois.cs.cogcomp.saul.datamodel.node.Node
import edu.illinois.cs.cogcomp.saul.datamodel.property.Property

/** Created by Parisa on 1/4/16.
  */
object evaluation {

  def Test[T <: AnyRef](ground_truth: Property[T], actual: Property[T], ds: Node[T]): Unit = {
    val r1 = ds.getTestingInstances
    def allmeasures(className: String, tp: Double, fp: Double, tn: Double, fn: Double) = {
      println("\n---------------------------------------------------------------------------")

      print(s"$className      \t")
      // print(s" class: $className tp: $tp fp: $fp tn: $tn fn: $fn ")
      if ((tp + fp) > 0)
        print(s"${tp / (tp + fp)} \t")
      else
        print(" 1\t")

      if ((tp + fn) > 0)
        print(s"${tp / (tp + fn)} \t")
      else
        print(" 1\t")
      if (2 * tp + fp + fn > 0)
        print(s"${(2.0 * tp) / (2 * tp + fp + fn)} \t")
      else
        print("   1\t")

      print(s"$tp\t$fp\t$tn\t$fn")
    }
    var tp_total = 0.0
    var fp_total = 0.0
    var tn_total = 0.0
    var fn_total = 0.0

    val results = r1.map({
      x =>
        val pri = actual(x)
        val truth = ground_truth(x)
        (pri, truth)
    })

    val allClasses = results.map(x => x._1).toList.distinct.union(results.map(x => x._2).toList.distinct).distinct
    print("\n Class\t Precision \tRecall\t F1\t \tTP\t FP\t TN\t FN ")
    allClasses.foreach {
      z =>
        val tp = results.count({ case (x, y) => x == y && (x == z) }) * 1.0
        val fp = results.count({ case (x, y) => x != y && (x == z) }) * 1.0

        val tn = results.count({ case (x, y) => x == y && (x != z) }) * 1.0
        val fn = results.count({ case (x, y) => x != y && (x != z) }) * 1.0
        allmeasures(z.toString, tp, fp, tn, fn)
        tp_total = tp_total + tp
        fn_total = fn_total + fn
        tn_total = tn_total + tn
        fp_total = fp_total + fp
    }

    allmeasures("Total:", tp_total, fp_total, tn_total, fn_total)
  }

}
