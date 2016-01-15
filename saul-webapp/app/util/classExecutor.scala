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

  //TODO: Add error handling
  def execute(className: String, classPath: String): Either[String, Error] = {
/*
    val cmd = Seq("scala", "-cp", classPath, className)
    val buffer = new StringBuilder
    def outputReader(input: java.io.InputStream) = {
      val reader = new BufferedReader(new InputStreamReader(input))
      while (true) {
        val line = reader.readLine()
        if (line eq null) {
          input.close()
          break
        } else {
          buffer.append(line)
        }
      }
    }
    cmd ! new ProcessIO(_.close(), outputReader, _.close())*/
    Left("Success")
  }
}
