/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package util

object QueryPreprocessor {

  val importStatement = "import util.VisualizerInstance._\n"

  /** This method is used for query processing from input box
    * and incorporate query into program body
    *
    * @param fileMap  source code
    * @param query    the query statement
    * @return         modified fileMap after preprocessing
    */
  def preprocess(fileMap: Map[String, String], query: String): Option[Map[String, String]] = {
    val fileName = findMain(fileMap)
    fileName match {
      case Some(file) => {
        val originCode = fileMap(file)
        val processedCode = insertVisualize(insertImport(originCode), query)
        Some(fileMap + ((file, processedCode)))
      }
      case None => None
    }
  }

  /** Find the file name of the class containing main method
    *
    * @param fileMap source code
    * @return        if main method exists, return the name. Else None.
    */
  private def findMain(fileMap: Map[String, String]): Option[String] = {
    val mainClass = fileMap find {
      case (_, code) => {
        " *def +main".r findFirstIn (code) match {
          case Some(m) => true
          case None => false
        }
      }
    }
    mainClass match {
      case Some(file) => Some(file._1)
      case None => None
    }
  }

  /** Insert default import of VisualizerInstances
    *
    * @param code  source code to be inserted
    * @return      source code after inserting import statement
    */
  private def insertImport(code: String): String = {
    val pack = " *package +.+\n".r findFirstMatchIn (code)
    //The index in the String where the import statement should be inserted
    val insertPos = pack match {
      case Some(m) => m.end
      case None => 0
    }
    code.substring(0, insertPos) + importStatement + code.substring(insertPos)
  }

  /** Wrap the query into visualize statement
    *
    * @param code  source code to be processed
    * @param query query to be incorporated
    * @return      source code after query is incorporated
    */
  private def insertVisualize(code: String, query: String): String = {
    val startMatch = " *def +main *(.+) *\\{".r findFirstMatchIn (code)
    val startIndex = startMatch match {
      case Some(m) => m.end
      case None => 0
    }
    val endMatch = "\\}".r findAllMatchIn (code)
    val endIndex = endMatch.filter(m => m.start >= startIndex).next.start
    code.substring(0, endIndex) + "visualize(" + query + ")\n" + code.substring(endIndex)
  }

}
