/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package util

import scala.util.matching.Regex

object ReflectUtils {



  def getCodePackageName(code: String) = {

    //Example input: package somePackageName;
    val re = """package\s(.*)\s""".r
    extractUsingRegex(code, re)

  }

  def extractUsingRegex(code:String, regex:Regex) = {
    (regex findFirstIn code) match {
      case Some(v) => (v.split(" "))(1).replaceAll("\\n", "").replaceAll(";", "").replace("\\n","")
      case None => play.api.Logger.error("Could not find package name."); ""
    }
  }

  def classPathOfClass(className: String) = {
    val resource = className.split('.').mkString("/", "/", ".class")
    var path = ""
    try {
      path = getClass.getResource(resource).getPath
    } catch {
      case e: Exception => play.api.Logger.info(resource + " not found.\n")
    }
    if (path.indexOf("file:") >= 0) {
      //using path after 5 characters which excludes "file:"
      val indexOfFile = path.indexOf("file:") + 5
      val indexOfSeparator = path.lastIndexOf('!')
      List(path.substring(indexOfFile, indexOfSeparator))
    } else {
      require(path.endsWith(resource))
      List(path.substring(0, path.length - resource.length + 1))
    }
  }

}
