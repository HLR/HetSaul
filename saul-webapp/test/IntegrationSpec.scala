/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._

/** add your integration spec here.
  * An integration test will fire up a whole play application in a real (or headless) browser
  */
@RunWith(classOf[JUnitRunner])
class IntegrationSpec extends Specification {

  "Application" should {

    "work from within a browser" in new WithBrowser {

      browser.goTo("http://localhost:" + port)

      browser.pageSource must contain("Declarative Learning-Based Programming")
    }
  }
}
