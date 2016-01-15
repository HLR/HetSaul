package controllers

import play.api.mvc._

import play.api.libs.json.{ JsValue, JsObject, Json }
import play.api.Logger

>>>>>>> Finish run event and add executor
import io.Source
import edu.illinois.cs.cogcomp.saul.datamodel.{ DataModel, dataModelJsonInterface }
import java.io.File
import java.nio.file.Files
import javax.tools._

import scala.collection.mutable
import java.net.{URLClassLoader,URL}
import java.nio.charset.StandardCharsets
import util.classExecutor

import scala.collection.JavaConverters._
import java.lang.reflect.Method
import scala.tools.nsc.{Global, Settings}
import scala.reflect.internal.util.{BatchSourceFile, Position}

import scala.reflect.runtime.universe
import scala.tools.nsc.reporters.{Reporter, AbstractReporter}

class Application extends Controller {

  //val saulExternalLibs = new File(classPathOfClass("edu.illinois.cs.cogcomp.lbjava.parse.Parser")(0)).getParentFile().getParentFile().getParentFile().getPath()
  //val resolvedSaulExternalLibs = if(saulExternalLibs.endsWith(File.separator)) (saulExternalLibs+"*") else (saulExternalLibs + File.separator + "*")
  val completeClasspath = (List("/tmp/") ::: classPathOfClass("scala.tools.nsc.Interpreter") ::: classPathOfClass("scala.AnyVal") ::: classPathOfClass("edu.illinois.cs.cogcomp.saul.datamodel.DataModel") ::: classPathOfClass("edu.illinois.cs.cogcomp.lbjava.parse.Parser") ::: classPathOfClass("edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation") ::: classPathOfClass("edu.illinois.cs.cogcomp.nlp.pipeline.IllinoisPipelineFactory") ::: classPathOfClass("edu.illinois.cs.cogcomp.curator.CuratorFactory")).mkString(File.pathSeparator)
  val re = """package\s(.*)\s""".r
  val root: File = new File("/tmp"); // On Windows running on C:\, this is C:\java.
  val rootURL = root.toURI.toURL
  val classLoader = URLClassLoader.newInstance(Array(rootURL), this.getClass().getClassLoader());
  val method: Method = new URLClassLoader(Array(rootURL)).getClass().getDeclaredMethod("addURL", rootURL.getClass())
  method.setAccessible(true)
  method.invoke(ClassLoader.getSystemClassLoader(), rootURL)
  method.invoke(ClassLoader.getSystemClassLoader(), new File(classPathOfClass("scala.tools.nsc.Interpreter")(0)).toURI.toURL)
  method.invoke(ClassLoader.getSystemClassLoader(), new File(classPathOfClass("scala.AnyVal")(0)).toURI.toURL)
  method.invoke(ClassLoader.getSystemClassLoader(), new File(classPathOfClass("edu.illinois.cs.cogcomp.saul.datamodel.DataModel")(0)).toURI.toURL)
  method.invoke(ClassLoader.getSystemClassLoader(), new File(classPathOfClass("edu.illinois.cs.cogcomp.lbjava.parse.Parser")(0)).getParentFile().getParentFile().getParentFile().toURI.toURL)
  
  trait MessageCollector {
      val messages: Seq[List[String]]
  }
  class CompilerException(val messages: List[List[String]]) extends Exception(
    "Compiler exception " + messages.map(_.mkString("\n")).mkString("\n"))

  def addPathToClasspath(file : File) = {
      method.invoke(ClassLoader.getSystemClassLoader(), file.toURI().toURL());

  }
  def index = Action { implicit request =>
    Ok(views.html.main("Your new application is ready."))
  }

  //TODO: Further refactor the two methods
  def compileCode = Action(parse.json) { implicit request =>

    request.body match {
      case files: JsObject => {
        val fileMap = files.as[Map[String, String]]
        val compilationResult = compile(fileMap)
        compilationResult match {
          case scalaInstances: Iterable[Any] => Ok(parseDataModel(scalaInstances))
          case JsValue => Ok(JsValue)
          case _ => Ok("Error. ")
        }
      }

      case _ => Ok("Bad json." + request.body)
    }

  }

  def runCode = Action(parse.json) { implicit request =>

    request.body match {
      case files: JsObject => {
        val fileMap = files.as[Map[String, String]]
        val compilationResult = compile(fileMap)
        compilationResult match {
          case scalaInstances: Iterable[Any] => Ok(runMain(scalaInstances))
          case JsValue => Ok(JsValue)
          case _ => Ok("Error. ")
        }

      }
      case _ => Ok("Bad json." + request.body)
    }
  }

  private def runMain(scalaInstances: Iterable[Any]): JsValue = {

    val result = scalaInstances find (x => classExecutor.containsMain(x))

    result match {

      case Some(x) => {
        x match {
          case ob: Object => {
            val output = classExecutor.execute(ob.getClass.getName.init, completeClasspath)
            output match {
              case Left(s) => Json.toJson(s)
              case Right(s) => Json.toJson("Error")
            }
          }
          case _ => Json.toJson("Error.")
        }
      }
      case None => Json.toJson("No main method found.")
    }
  }


  private def compile(fileMap: Map[String, String]) = {


    val (javaFiles, scalaFiles) = fileMap partition {
      case (k, _) => k contains ".java"
    }

    compileJava(javaFiles)

    try {
      Left(compileScala(scalaFiles))
    } catch {
      case _ => Right(getErrorJson(Json.toJson(errors.messages)))
    }
  }

  def getErrorJson(content : JsValue) : JsValue ={
    JsObject(Seq("error" -> content))
  }

  def parseDataModel(scalaInstances: Iterable[Any]) : JsValue = {

    compileScala(scalaFiles)
  }


  private def runMain(scalaInstances: Iterable[Any]): JsValue = {

    val result = scalaInstances find (x => classExecutor.containsMain(x))

    result match {

      case Some(x) => {
        x match {
          case ob: Object => {
            val output = classExecutor.execute(ob.getClass.getName.init, completeClasspath)
            output match {
              case Left(s) => Json.toJson(s)
              case Right(s) => Json.toJson("Error")
            }
          }
          case _ => Json.toJson("Error.")
        }
      }
      case None => Json.toJson("No main method found.")
    }
  }


  private def parseDataModel(scalaInstances: Iterable[Any]): JsValue = {
    val result = scalaInstances find (x => x match {
      case model: DataModel => true
      case _ => false
    })

    result match {

      case Some(x) => x match {

        case model: DataModel => dataModelJsonInterface.getJson(model)
        case _ => Json.toJson("Error")
      }
      case _ => Json.toJson("No DataModel found.")
    }
  }
  

  /*
     *    * For a given FQ classname, trick the resource finder into telling us the containing jar.
     *
     *
     * */
  private def classPathOfClass(className: String) = {
    val resource = className.split('.').mkString("/", "/", ".class")
    val path = getClass.getResource(resource).getPath
    if (path.indexOf("file:") >= 0) {
      val indexOfFile = path.indexOf("file:") + 5
      val indexOfSeparator = path.lastIndexOf('!')
      List(path.substring(indexOfFile, indexOfSeparator))
    } else {
      require(path.endsWith(resource))
      List(path.substring(0, path.length - resource.length + 1))
    }
  }

  def writeCodeToFiles(files: Map[String, String]) = {
    files map {
      case (k, v) => {
        // Save source in .java file.
        val sourceFile: File = new File(root, "test/" + k);
        sourceFile.getParentFile().mkdirs();
        Files.write(sourceFile.toPath(), v.split("\\n").toList.asJava, StandardCharsets.UTF_8);
        sourceFile.getPath()
      }
    } toList
  }


  def getScalaCompilerReporter(setting : Settings) = new AbstractReporter with MessageCollector {

      //for displaying compiler error message
      val settings = setting
      val messages = new mutable.ListBuffer[List[String]]

      def display(pos: Position, message: String, severity: Severity) {
        severity.count += 1
        val severityName = severity match {
          case ERROR   => "error: "
          case WARNING => "warning: "
          case _ => ""
        }
        // the line number is not always available
        val lineMessage =
          try {
            "line " + (pos.line - 0)
          } catch {
            case _: Throwable => ""
          }
        messages += (severityName + lineMessage + ": " + message) ::
          (if (pos.isDefined) {
            pos.inUltimateSource(pos.source).lineContent.stripLineEnd ::
              (" " * (pos.column - 1) + "^") ::
              Nil
          } else {
            Nil
          })
      }

      def displayPrompt {
        // no.
      }

      override def reset {
        super.reset
        messages.clear()
      }
  }

  def compileScala(files: Map[String, String]): Iterable[Any] = files map {


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

      val reporter = getScalaCompilerReporter(sett)
      val g = new Global(sett,reporter)
      val run = new g.Run        
      run.compileSources(sourceFiles.toList)

      //Ignore warnings for now
      if (reporter.hasErrors /*|| reporter.WARNING.count > 0*/) {
        val msgs: List[List[String]] = reporter match {
          case collector: MessageCollector =>
            collector.messages.toList
          case _ =>
            List(List(reporter.toString))
        }
        throw new CompilerException(msgs)
      }
      val clazz = instantiateClass(name,getCodePackageName(code))

      clazz
    }

  }

  def compileJava(files: Map[ String,String]) : Unit= {
    play.api.Logger.info("Compiling Java code.")

    val names = files map {
      case (k, v) => {
        // Save source in .java file.
        val filePath = getCodePackageName(v).replaceAll("\\.", "/") + "/" + k
        val sourceFile: File = new File(root, filePath);
        sourceFile.getParentFile().mkdirs();
        Files.write(sourceFile.toPath(), v.split("\\n").toList.asJava, StandardCharsets.UTF_8);
        sourceFile.getPath()
      }
    } toList

    if(names.isEmpty) return
    
    val names2 = List("-classpath") ::: List(completeClasspath) ::: names

    // Compile source file.
    val compiler: JavaCompiler = ToolProvider.getSystemJavaCompiler();
    compiler.run(null, null, null, names2: _*);

  }

  def cleanUpTmpFolder() = {
    val file: File = new File("/tmp")
    cleanUpFolder(file)
  }
  def cleanUpFolder(file: File): Array[(String, Boolean)] = {
    Option(file.listFiles).map(_.flatMap(f => cleanUpFolder(f))).getOrElse(Array()) :+ (file.getPath -> file.delete)
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
    //val clazz = classLoader.loadClass(packageName + "." + name(0)) 
    //val constructor = clazz.getConstructor()
    //constructor.setAccessible(true)
    //val instance = constructor.newInstance()
    val module = runtimeMirror.staticModule(packageName + "." + name(0))

    val obj = runtimeMirror.reflectModule(module)

    obj.instance
  }
  def getCodePackageName(code: String) = {
    (re findFirstIn code) match {
      case Some(v) => (v.split(" "))(1).replaceFirst("\\n", "").replaceFirst(";", "")
      case None => play.api.Logger.error("Could not find package name."); ""
    }
  }
}
