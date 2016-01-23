package util

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files

object IOUtils {

  //TODO: is this function used?
  /*
  def writeCodeToFiles(files: Map[String, String], root: File) = {
    files map {
      case (k, v) => {
        // Save source in .java file.
        val sourceFile: File = new File(root, "test/" + k);
        sourceFile.getParentFile().mkdirs();
        Files.write(sourceFile.toPath(), v.split("\\n").toList.asJava, StandardCharsets.UTF_8);
        sourceFile.getPath()
      }
    } toList
  }*/

  def cleanUpTmpFolder(DirPath : String) = {
    cleanUpFolder(new File(DirPath))
  }
  def cleanUpFolder(file: File): Array[(String, Boolean)] = {
    Option(file.listFiles).map(_.flatMap(f => cleanUpFolder(f))).getOrElse(Array()) :+ (file.getPath -> file.delete)
  }

}
