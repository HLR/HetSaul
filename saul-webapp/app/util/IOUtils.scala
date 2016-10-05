/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package util

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._

object IOUtils {

  def cleanUpTmpFolder(DirPath: String) = {
    cleanUpFolder(new File(DirPath))
  }

  def cleanUpFolder(file: File): Array[(String, Boolean)] = {
    Option(file.listFiles).map(_.flatMap(f => cleanUpFolder(f))).getOrElse(Array()) :+ (file.getPath -> file.delete)
  }

  def findLeafFolders(folder: String) = {
    val sourceFile: File = new File(folder)
    var l = new ListBuffer[File]()
    addLeafFolderToList(sourceFile,l)
    l.map(_.getName()).toList
  }

  private def addLeafFolderToList(file: File, list : ListBuffer[File]) : Unit= {
    file.listFiles().exists(_.isDirectory()) match {
      case true => {
        for(f <- file.listFiles()){
          f.isDirectory() match{
            case true => addLeafFolderToList(f,list)
            case _ =>
          }
        }
      }
      case _ => list += file
    }
  }

  def getExampleFileContentList(dir: String,projectName : String):Map[String,List[String]] = {
    var l = new ListBuffer[File]()
    addLeafFolderToList(new File(dir),l)
    l.find(_.getName() == projectName) match{
      case Some(file) => {
        val result = file.listFiles().filter(x=> x.getName().contains(".java") || x.getName().contains(".scala")).map{
          case file => {
            val content = replacePackageName(file,"test")
            (file.getName(),content)
          }
        }

        result.toMap
      }
      case _ => Map[String,List[String]]()
    }
  }

  def replacePackageName(file : File , packageName: String) = {
    val contentArray = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8)
    val content = contentArray.mkString("\\n")
    val oldPackage = ReflectUtils.extractUsingRegex(content,"""package\s(\.|\w|;)*\\n""".r)
    contentArray.map(_.replace(oldPackage,packageName)).toList
  }
}
