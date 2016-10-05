/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package controllers

import controllers.Event._
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import logging.Logger
import play.api.mvc._
import play.api.libs.json._
import java.io.File
import scala.collection.mutable
import scala.reflect.internal.util.{ BatchSourceFile, Position }
import scala.tools.nsc.Settings
import scala.tools.nsc.reporters.{ Reporter, AbstractReporter }
import _root_.util.ReflectUtils._
import _root_.util._

object Application {

  val rootDir = "/tmp"
  val exampleDir = "./saul-examples/src/main/scala/edu/illinois/cs/cogcomp/saulexamples"
  val completeClasspath = (List(
    "scala.tools.nsc.Interpreter",
    "scala.AnyVal",
    "edu.illinois.cs.cogcomp.edison.features.factory.WordFeatureExtractorFactory",
    "edu.illinois.cs.cogcomp.edison.features.FeatureExtractor",
    "edu.illinois.cs.cogcomp.saul.util.Logging",
    "edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.SpamApp",
    "edu.illinois.cs.cogcomp.saulexamples.DrugResponse.myApp",
    "edu.illinois.cs.cogcomp.saul.datamodel.DataModel",
    "edu.illinois.cs.cogcomp.lbjava.parse.Parser",
    "edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation",
    "edu.illinois.cs.cogcomp.nlp.pipeline.IllinoisPipelineFactory",
    "edu.illinois.cs.cogcomp.curator.CuratorFactory",
    "ch.qos.logback.classic.Level",
    "ch.qos.logback.classic.encoder.PatternLayoutEncoder",
    "ch.qos.logback.classic.html.HTMLLayout",
    "ch.qos.logback.classic.spi.ILoggingEvent",
    "ch.qos.logback.core.encoder.Encoder",
    "ch.qos.logback.core.encoder.LayoutWrappingEncoder",
    "org.slf4j.impl.StaticLoggerBinder",
    "org.slf4j.LoggerFactory",
    "util.VisualizerInstance"
  ).flatMap(x => classPathOfClass(x)) ::: List(rootDir)).mkString(File.pathSeparator)
}

class Application extends Controller {

  import Application._

  val compiler = new Compiler(rootDir, completeClasspath, getScalaCompilerReporter)

  def index = Action { implicit request =>
    Ok(views.html.main("Saul Visualization Web Interface"))
  }

  def graph = Action { implicit request =>
    Ok(views.html.graph("Graph Visualization"))
  }

  def plot = Action { implicit request =>
    Ok(views.html.plot("Plot Visualization"))
  }

  def getExamples = Action(parse.json) { implicit request =>

    Ok(Json.toJson(IOUtils.findLeafFolders(exampleDir)))
  }

  def getExampleFile = Action(parse.json) { implicit request =>
    val files = parseRequest(GetExample(), request)
    files match {
      case Some(fileMap) => {
        val (k,v) = fileMap.head
        Ok(Json.toJson(IOUtils.getExampleFileContentList(exampleDir,v)))

      }
      case _ => Ok(getErrorJson(Json.toJson("No filemap found.")))
    }
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

  def acceptQuery = Action(parse.json) { implicit request =>
    execute(Query(), request)
  }

  def visualize = Action(parse.json) { implicit request =>
    IOUtils.cleanUpTmpFolder(rootDir)
    val files = parseRequest(RunMain(), request)
    files match {
      case Some(fileMap) => {
        val compilationResult = compile(fileMap)
        compilationResult match {
          case Left(scalaInstances) => {
            val dm = getDataModel(scalaInstances)
            dm match {
              case Some(dataModel) => {
                val dataModelSchema = DataModelJsonInterface.getSchemaJson(dataModel)
                //If main class found, execute and get output and data population results
                val executionResult = executeMain(scalaInstances, fileMap, compiler)
                executionResult match {
                  case Some(x) =>
                    Ok(JsObject(Seq(
                      "dataModelSchema" -> dataModelSchema,
                      "populatedModel" -> DataModelJsonInterface.getPopulatedInstancesJson(dataModel),
                      "log" -> JsObject(Logger.gather.map(record => (record._1, JsString(record._2))))
                    )))
                  case None =>
                    Ok(JsObject(Seq(
                      "dataModelSchema" -> dataModelSchema,
                      "populatedModel" -> JsNull,
                      "log" -> JsNull
                    )))
                }
              }
              case None => Ok(getErrorJson(Json.toJson("No DataModel found.")))
            }
          }
          case Right(errorMsg) => Ok(errorMsg)
        }
      }
    }
  }

  private def getDataModel(scalaInstances: Iterable[Any]): Option[DataModel] = {
    val result = scalaInstances find {
      case model: DataModel => true
      case _ => false
    }

    result match {
      case Some(x) => x match {
        case model: DataModel => Some(model)
        case _ => None
      }
      case None => None
    }
  }

  private def executeMain(scalaInstances: Iterable[Any], fileMap: Map[String, String], compiler: Compiler): Option[Unit] = {
    scalaInstances find (x => ClassExecutor.containsMain(x)) match {
      case Some(x) => {
        VisualizerInstance.init
        Logger.init
        Some(compiler.executeWithoutLog(x))
      }
      case None => None
    }
  }

  private def parseRequest(event: Event, request: Request[JsValue]): Option[Map[String, String]] = {
    request.body match {
      case jsonData: JsObject =>
        event match {
          case Query() =>
            val dataMap = jsonData.as[Map[String, String]]
            val query = dataMap("query")
            val files = Json.parse(dataMap("files")).as[Map[String, String]]
            val fileMap = QueryPreprocessor.preprocess(files, query)
            fileMap
          case _ => Some(jsonData.as[Map[String, String]])
        }
      case _ => None
    }
  }

  private def execute(event: Event, request: Request[JsValue]) = {

    IOUtils.cleanUpTmpFolder(rootDir)
    val files = parseRequest(event, request)
    files match {
      case Some(fileMap) => {
        val compilationResult = compile(fileMap)
        compilationResult match {
          case Left(scalaInstances) =>
            event match {
              case DisplayModel() => Ok(displayModel(scalaInstances))
              case PopulateData() => Ok(populateModel(scalaInstances, fileMap, compiler))
              case RunMain() => Ok(runMain(scalaInstances))
              case Query() => Ok(populateModel(scalaInstances, fileMap, compiler))
            }
          case Right(errorMsg) => Ok(errorMsg)
        }
      }
      case None => Ok("Bad json.")
    }
  }

  private def displayModel(scalaInstances: Iterable[Any]): JsValue = {
    //Assume there is only one DataModel in the files
    scalaInstances find (x => x match {
      case model: DataModel => return DataModelJsonInterface.getSchemaJson(model)
      case _ => false
    })
    return getErrorJson(Json.toJson("No DataModel found."))
  }

  private def populateModel(scalaInstances: Iterable[Any], fileMap: Map[String, String], compiler: Compiler): JsValue = {
    scalaInstances find (x => ClassExecutor.containsMain(x)) match {
      case Some(x) => {
        VisualizerInstance.init
        compiler.executeWithoutLog(x)
        scalaInstances find (x => x match {
          case model: DataModel => return DataModelJsonInterface.getPopulatedInstancesJson(model).as[JsObject] + ("Statistics" -> Statistics.getStatistics(model))
          case _ => false
        })
        return getErrorJson(Json.toJson("No DataModel found."))
      }
      case _ => getErrorJson(Json.toJson("No main method found.(Try changing the filename if you are certain you have a main method)"))
    }
  }

  private def runMain(scalaInstances: Iterable[Any]): JsValue = {
    val result = scalaInstances find (x => ClassExecutor.containsMain(x))
    result match {
      case Some(x) => {
        x match {
          case ob: Object => {
            val output = ClassExecutor.execute(ob.getClass.getName.init, completeClasspath)
            Json.obj(
              "stdout" -> output._1,
              "stderr" -> output._2,
              "status" -> output._3
            )
          }
          case _ => Json.toJson("Error.")
        }
      }
      case None => getErrorJson(Json.toJson("No main method found.(Try changing the filename if you are certain you have a main method)"))
    }
  }

  def getErrorJson(content: JsValue): JsValue = {
    JsObject(Seq("error" -> content))
  }

  private def compile(fileMap: Map[String, String]) = {
    val (javaFiles, scalaFiles) = fileMap partition {
      case (k, v) => k contains ".java"
    }

    compiler.compileJava(javaFiles)

    try {
      Left(compiler.compileScala(scalaFiles))
    } catch {
      case errors: CompilerException => Right(getErrorJson(Json.toJson(errors.messages)))
      case unknown => throw unknown //Right(Json.toJson("Unknown exception."))
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
      // Nothing for now
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
