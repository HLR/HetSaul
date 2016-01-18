package edu.illinois.cs.cogcomp.saul.datamodel

/*import edu.illinois.cs.cogcomp.lbjava.infer.OJalgoHook
import org.scalatest.{ FlatSpec, Matchers }

class OJAlgoHookTest extends FlatSpec with Matchers {
  "OJAAlgoHook" should " be able to solve a toy problem" in {
    val ojaHook = new OJalgoHook()

    val objCoefs = Array(1.5, 2.5)

    val varInds = List(0, 1).map(i => ojaHook.addBooleanVariable(objCoefs(i))).toArray

    val coefs = Array(1d, 2d)
    ojaHook.addGreaterThanConstraint(varInds, coefs, 1)
    ojaHook.addLessThanConstraint(varInds, coefs, 2)

    ojaHook.setMaximize(true)

    ojaHook.solve()

    ojaHook.objectiveValue() should be(2.5)
    ojaHook.getBooleanValue(0) should be(false)
    ojaHook.getBooleanValue(1) should be(true)
  }
}
*/ 