package controllers

import play.api.mvc._
import play.api.libs.json.{JsObject,Json}
import reflect.runtime.currentMirror
import tools.reflect.ToolBox
import io.Source
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import java.io.File

class Application extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.main("Your new application is ready."))
  }
  def updateCode = Action(parse.json) { implicit request =>
    
    request.body match {
      case files : JsObject => {
            val fileMap = files.as[Map[String, String]]

            //val result = fileMap map {
            //  case(k,v) => (k,runCode(v))
            //}

            val result = runCode(fileMap.map { case(k,v) => v }.mkString("\n"))

            Ok(Json.toJson(result))
      }

      case _ => Ok("Bad json." + request.body)
    }
  }

  def runCode(code : String) = {

    val toolbox = currentMirror.mkToolBox()
    val tree = toolbox.parse(code)
    val compiledCode = toolbox.compile(tree)
    
  
    //val model : DataModel = compiledCode.asInstanceOf[DataModel]
    //val eval = new Eval()
    //val greenhouse = eval.apply[DataModel](code) 
    //new File("./spamDataModel.scala")

  //compiler.compile(List("spamDataModel.scala","Document.java","DocumentReader.java"))  // invoke compiler. it creates Test.class.


    code
  }

}
