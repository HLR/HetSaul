package util

import java.io.File

object IOUtils {

  def cleanUpTmpFolder(DirPath: String) = {
    cleanUpFolder(new File(DirPath))
  }

  def cleanUpFolder(file: File): Array[(String, Boolean)] = {
    Option(file.listFiles).map(_.flatMap(f => cleanUpFolder(f))).getOrElse(Array()) :+ (file.getPath -> file.delete)
  }

}
