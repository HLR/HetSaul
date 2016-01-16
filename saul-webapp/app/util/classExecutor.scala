package util

import java.io.{ InputStreamReader, BufferedReader }

import scala.sys.process._

object classExecutor {

  def containsMain(clazz: Any): Boolean = {
    clazz match {
      case ob: Object => {
        val mainEntry = ob.getClass.getMethods find (x => x.getName eq "main")
        mainEntry match {
          case Some(x) => true
          case None => false
        }
      }
      case _ => false
    }
  }

  /** @param className The main class to execute
    * @param classPath The classpath argument
    * @return Output from STDOUT, output from STDERR, value return on exit
    */
  //TODO: further handle exception
  def execute(className: String, classPath: String): (String, String, Int) = {

    val cmd = Seq("scala", "-cp", classPath, className)
    val outBuffer = new StringBuilder
    val errBuffer = new StringBuilder
    val outputLogger = ProcessLogger(
      line => outBuffer.append(line),
      line => errBuffer.append(line)
    )
    val status = cmd ! outputLogger
    (outBuffer.toString(), errBuffer.toString(), status)
  }
}
