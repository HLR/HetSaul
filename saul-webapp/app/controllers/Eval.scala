package controllers

import play.api.libs.json.{ Json, JsValue }

//TODO: define eval function in object or class?
object Eval {

  //@Joe in case you also need the full source code I also pass
  // it in now, if it is not used I will refactor it later
  def eval(scalaInstances: Iterable[Any], sourceCode: String): JsValue = {
    Json.toJson("Error")
  }
}

class Eval {

}