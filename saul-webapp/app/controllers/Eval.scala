package controllers

import play.api.libs.json.{ Json, JsValue }

//TODO: define eval function in object or class?
object Eval {

  //TODO: implement this
  //@Joe in case you also need the full source code
  //and compiler I also pass it in. If not used remove it later
  def eval(scalaInstances: Iterable[Any], fileMap: Map[String, String], compiler: Compiler): JsValue = {
    Json.toJson("Error")
  }
}

class Eval {

}