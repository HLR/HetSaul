package logging

//TODO: change to stateless logger to handle asynchronous conditions
object logger {

  val collector: Map[String, StringBuilder] = Map[String, StringBuilder](
    "info" -> new StringBuilder(),
    "error" -> new StringBuilder()
  )

  def init = { collector foreach (record => record._2.clear) }

  def info(message: String): Unit = {
    collector.get("info") match {
      case Some(info) => info.append(message)
    }
  }

  def error(message: String): Unit = {
    collector.get("error") match {
      case Some(error) => error.append(message)
    }
  }

  def gather: Map[String, String] = { collector map (record => (record._1, record._2.toString)) }
}

