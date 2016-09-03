/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package logging

object Logger {

  val collector: Map[String, StringBuilder] = Map[String, StringBuilder](
    "info" -> new StringBuilder(),
    "error" -> new StringBuilder()
  )

  def init = collector foreach { case (_, record) => record.clear }

  def info(message: String): Unit = {
    collector.get("info") match {
      case Some(info) => info.append(message)
    }
  }

  def error(message: String): Unit = {
    collector.get("error") match {
      case Some(error) => error.append(message)
    }
  }

  def gather: Map[String, String] = collector map { case (logType, record) => (logType, record.toString) }
}

