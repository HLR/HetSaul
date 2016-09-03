/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package controllers

import java.io.File
import java.lang.reflect.Method
import java.net.{ URL, URLClassLoader }
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import javax.tools.{ ToolProvider, JavaCompiler }
import scala.collection.JavaConverters._
import util.ReflectUtils._
import scala.reflect.internal.util.BatchSourceFile
import scala.reflect.runtime._
import scala.tools.nsc.{ Global, Settings }
import scala.tools.nsc.reporters.{ Reporter, AbstractReporter }

final class CompilerException(val messages: List[List[String]]) extends Exception(
  "Compiler exception " + messages.map(_.mkString("\n")).mkString("\n")
)

final class Compiler(rootDir: String, completeClasspath: String, reporterCallback: (Settings) => AbstractReporter) {

  val root: File = new File(rootDir); // On Windows running on C:\, this is C:\java.
  val rootURL = root.toURI.toURL
  var classLoader: URLClassLoader = null

  initializeClassLoader

  private def initializeClassLoader = {
    classLoader = URLClassLoader.newInstance(Array(rootURL), this.getClass().getClassLoader())
  }

  def compileJava(files: Map[String, String]): Unit = {
    play.api.Logger.info("Compiling Java code.")

    val names = files map {
      case (k, v) => {
        // Save source in .java file.
        // TODO: compatibility on Windows machines
        val filePath = getCodePackageName(v).replaceAll("\\.", "/") + "/" + k
        val sourceFile: File = new File(root, filePath);
        sourceFile.getParentFile().mkdirs();
        Files.write(sourceFile.toPath(), v.split("\\n").toList.asJava, StandardCharsets.UTF_8);
        sourceFile.getPath()
      }
    } toList

    if (names.isEmpty) return

    val names2 = List("-classpath") ::: List(completeClasspath) ::: names

    // Compile source file.
    val compiler: JavaCompiler = ToolProvider.getSystemJavaCompiler();
    compiler.run(null, null, null, names2: _*);
  }

  def compileScala(files: Map[String, String]): Iterable[Any] = {
    initializeClassLoader
    files map {
      case (name, code) => {
        play.api.Logger.info("Compiling Scala code.")
        val sourceFiles = files map { x: (String, String) =>
          x match {
            case (k, v) => new BatchSourceFile("(inline)", v)
          }
        }

        val sett = new Settings()
        sett.classpath.value = completeClasspath
        sett.bootclasspath.value = sett.classpath.value
        sett.outdir.value = "/tmp"

        val reporter = reporterCallback(sett)
        val g = new Global(sett, reporter)
        val run = new g.Run
        run.compileSources(sourceFiles.toList)

        //Ignore warnings for now
        if (reporter.hasErrors /*|| reporter.WARNING.count > 0*/ ) {
          val msgs: List[List[String]] = reporter match {
            case collector: MessageCollector =>
              collector.messages.toList
            case _ =>
              List(List(reporter.toString))
          }
          //TODO: use case class for error handling
          throw new CompilerException(msgs)
        }
        val clazz = instantiateClass(name, getCodePackageName(code))
        clazz
      }

    }
  }

  def getCurrentClasspath() = {
    val getDeclaredMethod: Method = new URLClassLoader(Array(rootURL)).getClass().getDeclaredMethod("getURLs")
    getDeclaredMethod.setAccessible(true);
    val result = getDeclaredMethod.invoke(ClassLoader.getSystemClassLoader()).asInstanceOf[Array[URL]]
    result
  }

  def instantiateClass(fileName: String, packageName: String) = {
    // Load and instantiate compiled class.
    val name = fileName.split('.')
    val runtimeMirror = universe.runtimeMirror(classLoader)
    try {
      val module = runtimeMirror.staticModule(packageName + "." + name(0))
      val obj = runtimeMirror.reflectModule(module)
      obj.instance
    } catch {
      case _ => throw new CompilerException(List(List("Instantiation Error. Did you get the path to the training data correct?")))
    }
  }

  def executeWithoutLog(file: Any): Unit = {
    val cls = file.getClass()
    cls.getMethods().find(x => x.getName eq "main") match {
      case Some(x) => {
        try {
          x.invoke(file, null)
        } catch {
          case e: java.lang.reflect.InvocationTargetException => e.printStackTrace()
        }
      }
      case _ =>
    }
  }

}
