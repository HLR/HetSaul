package controllers

import controllers.Event._

import edu.illinois.cs.cogcomp.saul.datamodel.{ DataModel, dataModelJsonInterface }

import play.api.mvc._
import play.api.libs.json.{ JsValue, JsObject, Json }

import java.io.File

import scala.collection.mutable
import scala.reflect.internal.util.{ BatchSourceFile, Position }
import scala.tools.nsc.Settings
import scala.tools.nsc.reporters.{ Reporter, AbstractReporter }

import util.reflectUtils._
import util.classExecutor

object Application {

  val rootDir = "/tmp"

  val completeClasspath = (List(
    "scala.tools.nsc.Interpreter",
    "scala.AnyVal",
    "edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.SpamApp",
    "edu.illinois.cs.cogcomp.saul.datamodel.DataModel",
    "edu.illinois.cs.cogcomp.lbjava.parse.Parser",
    "edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation",
    "edu.illinois.cs.cogcomp.nlp.pipeline.IllinoisPipelineFactory",
    "edu.illinois.cs.cogcomp.curator.CuratorFactory"
  ).flatMap(x => classPathOfClass(x)) ::: List(rootDir)).mkString(File.pathSeparator)

}

class Application extends Controller {

  import Application._

  val compiler = new Compiler(rootDir, completeClasspath, getScalaCompilerReporter)
  //val saulExternalLibs = new File(classPathOfClass("edu.illinois.cs.cogcomp.lbjava.parse.Parser")(0)).getParentFile().getParentFile().getParentFile().getPath()
  //val resolvedSaulExternalLibs = if(saulExternalLibs.endsWith(File.separator)) (saulExternalLibs+"*") else (saulExternalLibs + File.separator + "*")

  def index = Action { implicit request =>
    Ok(views.html.main("Your new application is ready."))
  }

  def acceptDisplayModel = Action(parse.json) { implicit request =>
    execute(DisplayModel(), request)
  }

  def acceptRunMain = Action(parse.json) { implicit request =>
    execute(RunMain(), request)
  }

  def acceptPopulateModel = Action(parse.json) { implicit request =>
    execute(PopulateData(), request)
  }

  private def execute(event: Event, request: Request[JsValue]) = {
    request.body match {
      case files: JsObject => {
        val fileMap = files.as[Map[String, String]]
        val compilationResult = compile(fileMap)
        compilationResult match {
          case Left(scalaInstances) => {
            event match {
              case DisplayModel() => Ok(displayModel(scalaInstances))
              case PopulateData() => Ok(populateModel(scalaInstances, fileMap, compiler))
              case RunMain() => Ok(runMain(scalaInstances))
            }
          }
          case Right(errorMsg) => Ok(errorMsg)
        }
      }

      case _ => Ok("Bad json." + request.body)
    }
  }

  private def displayModel(scalaInstances: Iterable[Any]): JsValue = {
    val result = scalaInstances find (x => x match {
      case model: DataModel => true
      case _ => false
    })

    result match {

      case Some(x) => x match {

        case model: DataModel => dataModelJsonInterface.getSchemaJson(model)
        case _ => Json.toJson("Error")
      }
      case _ => Json.toJson("No DataModel found.")
    }
  }

  private def populateModel(scalaInstances: Iterable[Any], fileMap: Map[String, String], compiler: Compiler): JsValue = {
    scalaInstances find (x => classExecutor.containsMain(x)) match {
      case Some(x) => {
        compiler.executeWithoutLog(x)
        scalaInstances find (x => x match {
          case model: DataModel => true
          case _ => false
        }) match {
          case Some(x) => x match {
            case model: DataModel => dataModelJsonInterface.getPopulatedInstancesJson(model)
            case _ => Json.toJson("Error")
          }
          case _ => Json.toJson("No DataModel found.")
        }
      }
      case _ => Json.toJson("No main method found.")
    }
    //Eval.eval(scalaInstances, fileMap, compiler)
  }

  private def runMain(scalaInstances: Iterable[Any]): JsValue = {

    val result = scalaInstances find (x => classExecutor.containsMain(x))

    result match {

      case Some(x) => {
        x match {
          case ob: Object => {
            val output = classExecutor.execute(ob.getClass.getName.init, completeClasspath)
            Json.obj(
              "stdout" -> output._1,
              "stderr" -> output._2,
              "status" -> output._3
            )
          }
          case _ => Json.toJson("Error.")
        }
      }
      case None => Json.toJson("No main method found.")
    }
  }

  def getErrorJson(content: JsValue): JsValue = {
    JsObject(Seq("error" -> content))
  }

  private def compile(fileMap: Map[String, String]) = {

    val (javaFiles, scalaFiles) = fileMap partition {
      case (k, _) => k contains ".java"
    }

    compiler.compileJava(javaFiles)

    try {
      Left(compiler.compileScala(scalaFiles))
    } catch {
      case errors: CompilerException => Right(getErrorJson(Json.toJson(errors.messages)))
      case _ => Right(Json.toJson("Unknown exception."))
    }
  }

  def getScalaCompilerReporter(setting: Settings) = new AbstractReporter with MessageCollector {

    //for displaying compiler error message
    val settings = setting
    val messages = new mutable.ListBuffer[List[String]]

    def display(pos: Position, message: String, severity: Severity) {
      severity.count += 1
      val severityName = severity match {
        case ERROR => "error: "
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

}

trait MessageCollector {
  val messages: Seq[List[String]]
}
