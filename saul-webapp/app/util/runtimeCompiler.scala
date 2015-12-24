import scala.tools.nsc.{Global, Settings}
import java.io._

object runtimeCompiler{

    def main(args: Array[String]): Unit = {
  val global = new Global(new Settings()) 

  val compiler = new global.Run  

  compiler.compile(List("spamDataModel.scala","Document.java","DocumentReader.java"))  // invoke compiler. it creates Test.class.

  val classLoader = new java.net.URLClassLoader(
    Array(new File(".").toURI.toURL),  // Using current directory.
    this.getClass.getClassLoader)

  val clazz = classLoader.loadClass("Test") // load class 

  clazz.newInstance  // create an instance, which will print Hello World.
}
}