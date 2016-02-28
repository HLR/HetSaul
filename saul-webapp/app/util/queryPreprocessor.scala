package util

object queryPreprocessor {

  val importStatement = "import util.visualizer._\n"

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

  private def findMain(fileMap: Map[String, String]): Option[String] = {
    val mainClass = fileMap find (file => {
      val code = file._2
      " *def +main".r findFirstIn (code) match {
        case Some(m) => true
        case None => false
      }
    })
    mainClass match {
      case Some(file) => Some(file._1)
      case None => None
    }
  }

  private def insertImport(code: String): String = {
    val pack = " *package +.+\n".r findFirstMatchIn (code)
    //The index in the String where the import statement should be inserted
    val insertPos = pack match {
      case Some(m) => m.end
      case None => 0
    }
    code.substring(0, insertPos) + importStatement + code.substring(insertPos)
  }

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
