/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.datamodel

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import org.scalatest.{ FlatSpec, Matchers }

class JoinTest extends FlatSpec with Matchers {

  "populating one child" should "should populate parent" in {
    object TestObject extends DataModel {
      val na = node[String]
      val nb = node[String]
      val nj = join(na, nb)(_.charAt(0) == _.charAt(0))
    }

    import TestObject._

    na().instances.size should be(0)
    nb().instances.size should be(0)
    nj().instances.size should be(0)

    na.addInstance("Sameer")
    na().instances.size should be(1)
    nb().instances.size should be(0)
    nj().instances.size should be(0)

    na.addInstance("Sebastian")
    na().instances.size should be(2)
    nb().instances.size should be(0)
    nj().instances.size should be(0)

    nb.addInstance("Jacobs")
    na().instances.size should be(2)
    nb().instances.size should be(1)
    nj().instances.size should be(0)

    nb.addInstance("Singh")
    na().instances.size should be(2)
    nb().instances.size should be(2)
    nj().instances.size should be(2)

    na.addInstance("John")
    na().instances.size should be(3)
    nb().instances.size should be(2)
    nj().instances.size should be(3)
  }

  "populating parent" should "should populate children" in {
    object TestObject extends DataModel {
      val na = node[String]
      val nb = node[String]
      val nj = join(na, nb)(_.charAt(0) == _.charAt(0))
    }

    import TestObject._

    na().instances.size should be(0)
    nb().instances.size should be(0)
    nj().instances.size should be(0)

    nj.addInstance("Sameer" -> "Singh")
    na().instances.size should be(1)
    nb().instances.size should be(1)
    nj().instances.size should be(1)

    nj.addInstance("Sebastian" -> "Singh")
    na().instances.size should be(2)
    nb().instances.size should be(1)
    nj().instances.size should be(2)
  }
}
