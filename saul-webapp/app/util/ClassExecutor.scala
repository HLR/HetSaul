/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package util

import java.io.{ InputStreamReader, BufferedReader }

import scala.sys.process._

object ClassExecutor {

  def containsMain(clazz: Any): Boolean = {
    clazz match {
      case ob: Object => {
        val mainEntry = ob.getClass.getMethods find (x => x.getName eq "main")
        mainEntry match {
          case Some(x) => true
          case None => false
        }
      }
      case _ => false
    }
  }

  /** Execute the main class
    * @param className The main class to execute
    * @param classPath The classpath argument
    * @return          Output from STDOUT, output from STDERR, value return on program exit
    */
  def execute(className: String, classPath: String): (List[String], List[String], Int) = {
    val cmd = Seq("scala", "-cp", classPath, className)
    var outBuffer = List[String]()
    var errBuffer = List[String]()
    val outputLogger = ProcessLogger(
      (line: String) => { outBuffer = outBuffer :+ (line + "\n") },
      (line: String) => { errBuffer = errBuffer :+ (line + "\n") }
    )
    val status = cmd ! outputLogger
    (outBuffer, errBuffer, status)
  }

}