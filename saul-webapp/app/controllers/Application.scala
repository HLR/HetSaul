package controllers

import play.api.mvc._
import play.api.libs.json.{JsObject,Json}
import io.Source
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import java.io.File
import java.nio.file.Files
import javax.tools._
import java.net.{URLClassLoader,URL}
import java.nio.charset.StandardCharsets
import scala.collection.JavaConverters._
import java.lang.reflect.Method
import scala.tools.nsc.{Global, Settings}
import scala.reflect.internal.util.{BatchSourceFile}

class Application extends Controller {

  val completeClasspath = (List("/tmp/") :::  classPathOfClass("scala.tools.nsc.Interpreter") ::: classPathOfClass("scala.AnyVal") ::: classPathOfClass("edu.illinois.cs.cogcomp.saul.datamodel.DataModel") ::: classPathOfClass("edu.illinois.cs.cogcomp.lbjava.parse.Parser")) .mkString(File.pathSeparator)
  val re = """package\s(.*)\s""".r
  val root:File = new File("/tmp"); // On Windows running on C:\, this is C:\java.
  val rootURL = root.toURI.toURL
  val classLoader = URLClassLoader.newInstance(Array(rootURL), this.getClass().getClassLoader() );
  val method : Method= new URLClassLoader(Array(rootURL)).getClass().getDeclaredMethod("addURL", rootURL.getClass())
  method.setAccessible(true)
  method.invoke(ClassLoader.getSystemClassLoader(), rootURL)
  method.invoke(ClassLoader.getSystemClassLoader(), new File(classPathOfClass("scala.tools.nsc.Interpreter")(0)).toURI.toURL)
  method.invoke(ClassLoader.getSystemClassLoader(), new File(classPathOfClass("scala.AnyVal")(0)).toURI.toURL)
  method.invoke(ClassLoader.getSystemClassLoader(), new File(classPathOfClass("edu.illinois.cs.cogcomp.saul.datamodel.DataModel")(0)).toURI.toURL)
  method.invoke(ClassLoader.getSystemClassLoader(), new File(classPathOfClass("edu.illinois.cs.cogcomp.lbjava.parse.Parser")(0)).toURI.toURL)
  
  def addPathToClasspath(file : File) = {
      method.invoke(ClassLoader.getSystemClassLoader(), file.toURI().toURL());
  }
  def index = Action { implicit request =>
    Ok(views.html.main("Your new application is ready."))
  }
  def updateCode = Action(parse.json) { implicit request =>
    
    request.body match {
      case files : JsObject => {
            val fileMap = files.as[Map[String, String]]

            val result = runCode(fileMap)
            //val result = runCode(fileMap.map { case(k,v) => v }.mkString("\n"))

            Ok(Json.toJson(result))
      }

      case _ => Ok("Bad json." + request.body)
    }
    
  }

  def runCode(fileMap: Map[String,String]) = {

    val (javaFiles,scalaFiles) = fileMap partition {
      case (k,_) => k contains ".java"
    }

    compileJava(javaFiles)
    compileScala(scalaFiles)
    "dummy"
  //instance.mkString 
  }

    /*
     *    * For a given FQ classname, trick the resource finder into telling us the containing jar.
     *       */
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
      case (k,v) => { 
        // Save source in .java file.
        val sourceFile:File = new File(root, "test/"+k);
        sourceFile.getParentFile().mkdirs();
        Files.write(sourceFile.toPath(),v.split("\\n").toList.asJava, StandardCharsets.UTF_8);
        sourceFile.getPath()
      }
    } toList
  }
  def compileScala(files:Map[String,String]) = files map {case(name,code)=>{

    play.api.Logger.info("Compiling Scala code.")
    //#1 method

    //val mirror = universe.runtimeMirror(classLoader)
    //val toolbox = mirror.mkToolBox()
    //val tree = toolbox.parse(code)
    //val compiledCode = toolbox.compile(tree)
    //val model = compiledCode()

    //#2 method
    //val eval = new Eval()
    //val greenhouse = eval.apply[DataModel](code) 
    //new File("./spamDataModel.scala")

    //#3
    val sett = new Settings()
    sett.classpath.value = completeClasspath
    println(sett.classpath.value)
    sett.bootclasspath.value = sett.classpath.value
    sett.outdir.value = "/tmp"
    val g = new Global(sett) 

    val run = new g.Run  

    val code =  files map { 
      case (k,v) => v
    } mkString("\\n")

    val sourceFiles = List(new BatchSourceFile("(inline)", code))
    run.compileSources(sourceFiles)
    
    val clazz = classLoader.loadClass(getCodePackageName(code) + "." + "spamDataModel") // load class 
  //compiler.compile(List("spamDataModel.scala","Document.java","DocumentReader.java"))  // invoke compiler. it creates Test.class.

  }
    
  }
  def compileJava(files: Map[ String,String]) = {
    play.api.Logger.info("Compiling Java code.")

    val names = files map { 
      case (k,v) => { 
        // Save source in .java file.
        val filePath = getCodePackageName(v).replaceAll("\\.","/")+"/"+k
        val sourceFile:File = new File(root, filePath);
        sourceFile.getParentFile().mkdirs();
        Files.write(sourceFile.toPath(),v.split("\\n").toList.asJava, StandardCharsets.UTF_8);
        sourceFile.getPath()
      }
    } toList

    val names2 = List("-classpath") ::: List(completeClasspath) ::: names

    // Compile source file.
    val compiler:JavaCompiler = ToolProvider.getSystemJavaCompiler();
    compiler.run(null, null, null, names2: _*);

  }

  def cleanUpTmpFolder() = {
    val file : File = new File("/tmp")
    cleanUpFolder(file)
  }
  def cleanUpFolder(file: File):Array[(String, Boolean)] = {
    Option(file.listFiles).map(_.flatMap(f => cleanUpFolder(f))).getOrElse(Array()) :+ (file.getPath -> file.delete)
  }

  def getCurrentClasspath() = {
    val method2 : Method= new URLClassLoader(Array(rootURL)).getClass().getDeclaredMethod("getURLs")
    method2.setAccessible(true);
    val result = method2.invoke(ClassLoader.getSystemClassLoader()).asInstanceOf[Array[URL]]
    result
  }
  def instantiateClass(fileName : String, packageName: String) = {
    // Load and instantiate compiled class.
    val name = fileName.split('.')
    val cls = Class.forName(packageName + "."+name(0), true, classLoader); 
    val instance = cls.newInstance(); // Should print "world".
    instance
  }
  def getCodePackageName(code: String) = {
    (re findFirstIn code) match {    
      case Some(v) => (v.split(" "))(1).replaceFirst("\\n", "").replaceFirst(";","")   
      case None => play.api.Logger.error("Could not find package name."); ""
    }
  }
}
