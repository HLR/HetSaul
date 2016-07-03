/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.ace

import java.io._
import java.nio.file._

import edu.illinois.cs.cogcomp.nlp.tokenizer.IllinoisTokenizer
import edu.illinois.cs.cogcomp.nlp.utility.CcgTextAnnotationBuilder
import edu.illinois.cs.cogcomp.reader.ace2005.annotationStructure.{ ACEDocument, ACEDocumentAnnotation }
import edu.illinois.cs.cogcomp.reader.ace2005.documentReader.{ AceFileProcessor, ReadACEDocuments, ReadACEAnnotation }
import edu.illinois.cs.cogcomp.reader.commondatastructure.XMLException
import edu.illinois.cs.cogcomp.saulexamples.nlp.ace.Types._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/** @author sameer
  * @since 12/24/15.
  */
class IllinoisReader {

  import scala.collection.JavaConversions._

  case class Ment(span: (Int, Int))
  def readMents(doc: ACEDocument): Seq[Ment] = {
    val ments = new mutable.HashSet[Ment]
    ments ++= doc.aceAnnotation.relationList.flatMap(_.relationMentionList).flatMap(_.relationArgumentMentionList).map(m => Ment(m.start -> m.end))
    ments ++= doc.aceAnnotation.entityList.flatMap(_.entityMentionList).map(m => Ment(m.extentStart -> m.extentEnd))
    ments.toSeq
  }

  def read(path: String): Seq[Document] = {
    val docs = new ArrayBuffer[Document]
    val in = new FileInputStream(path)
    val stream = new ObjectInputStream(in)
    val aceDoc = stream.readObject().asInstanceOf[ACEDocument]
    assert(aceDoc.taList.size() == 1, path)
    val ments = readMents(aceDoc)
    for (ta <- aceDoc.taList.map(_.getTa)) {
      val id = ta.getId
      val sents = ta.sentences().map(s => {
        val ms = ments.filter(m => m.span._1 >= s.getStartSpan && m.span._2 <= s.getEndSpan).map(m => Mention(m.span._1, m.span._2))
        val toks = s.getTokens.map(s => Token(s))
        val span = s.getStartSpan -> s.getEndSpan
        Sentence(s.getSentenceId, ms, toks, span)
      })
      val doc = Document(id, sents)
      docs += doc
    }
    docs
  }

  def readAll(baseDir: String = "data/ace/ace-2005/data/cache/"): Seq[Document] =
    new File(baseDir).list().map(baseDir + _).flatMap(f => read(f))
}

object TestACEReader extends App {
  (new IllinoisReader).readAll("/Users/sameer/Work/data/ace/ace-2005/data/cache/")
}

object AnnotateACE extends App {

  def annotateAllDocument(functor: AceFileProcessor, inputFolderStr: String, outputFolderStr: String, dtdFile: File) {
    val var20 = new File(inputFolderStr)
    val subFolderList = var20.listFiles().filter(f => f.getName == "nw")

    for (folderIndex <- 0 until subFolderList.length) {
      val filter = new FilenameFilter() {
        def accept(directory: File, fileName: String) = fileName.endsWith(".apf.xml")
      };
      val subFolderEntry = subFolderList(folderIndex)
      val labelFolder = new File(subFolderEntry.getAbsolutePath() + "/adj");
      val fileList = labelFolder.listFiles(filter);
      Files.copy(Paths.get(dtdFile.getAbsolutePath), Paths.get(labelFolder.getAbsolutePath + "/" + dtdFile.getName), StandardCopyOption.REPLACE_EXISTING)

      for (fileID <- 0 until fileList.length) {
        val annotationFile = fileList(fileID).getAbsolutePath();
        System.err.println("reading ace annotation from \'" + annotationFile + "\'...");
        var annotationACE: ACEDocumentAnnotation = null;

        try {
          annotationACE = ReadACEAnnotation.readDocument(annotationFile);
        } catch {
          case var19: XMLException => var19.printStackTrace(); System.exit(1)
        }

        val outputFile = new File(outputFolderStr + annotationACE.id + ".ta");
        if (!outputFile.exists()) {
          if (annotationFile.contains("rec.games.chess.politics_20041216.1047")) {
            System.out.println("[DEBUG]");
          }

          System.out.println("[File]" + annotationFile);
          val aceDoc = functor.processAceEntry(subFolderEntry, annotationACE, annotationFile);

          try {
            val f = new FileOutputStream(outputFile);
            val e = new ObjectOutputStream(f);
            e.writeObject(aceDoc);
            e.flush();
          } catch {
            case var18: Exception => var18.printStackTrace(); System.exit(1)
          }
        }
      }
    }

  }

  val baseDir = "/Users/sameer/Work/data/ace/ace-2005/data/"
  val dtdFile = "/Users/sameer/Work/data/ace/ace-2005/dtd/apf.v5.1.1.dtd"
  val docDirInput = baseDir + "English/"
  val docDirOuput = baseDir + "cache/"
  new File(docDirOuput).mkdirs
  val taBuilder = new CcgTextAnnotationBuilder(new IllinoisTokenizer())
  val functor = new AceFileProcessor(taBuilder)
  annotateAllDocument(functor, docDirInput, docDirOuput, new File(dtdFile))
}
