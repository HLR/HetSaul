package controllers

import play.api.mvc._
import play.api.libs.json.{JsObject,Json}
import io.Source
import edu.illinois.cs.cogcomp.saul.datamodel.{DataModel,dataModelJsonInterface}
import java.io.File
import java.nio.file.Files
import javax.tools._
import java.net.{URLClassLoader,URL}
import java.nio.charset.StandardCharsets
import scala.collection.JavaConverters._
import java.lang.reflect.Method
import scala.tools.nsc.{Global, Settings}
import scala.reflect.internal.util.{BatchSourceFile}
import scala.reflect.runtime.universe

class Application extends Controller {

  //val saulExternalLibs = new File(classPathOfClass("edu.illinois.cs.cogcomp.lbjava.parse.Parser")(0)).getParentFile().getParentFile().getParentFile().getPath()
  //val resolvedSaulExternalLibs = if(saulExternalLibs.endsWith(File.separator)) (saulExternalLibs+"*") else (saulExternalLibs + File.separator + "*")
  val completeClasspath = (List("/tmp/") :::  classPathOfClass("scala.tools.nsc.Interpreter") ::: classPathOfClass("scala.AnyVal") ::: classPathOfClass("edu.illinois.cs.cogcomp.saul.datamodel.DataModel") ::: classPathOfClass("edu.illinois.cs.cogcomp.lbjava.parse.Parser") ::: classPathOfClass("edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation")) .mkString(File.pathSeparator)
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
  method.invoke(ClassLoader.getSystemClassLoader(), new File(classPathOfClass("edu.illinois.cs.cogcomp.lbjava.parse.Parser")(0)).getParentFile().getParentFile().getParentFile().toURI.toURL)
  
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

            Ok(result)
      }

      case _ => Ok("Bad json." + request.body)
    }
    
  }

  def runCode(fileMap: Map[String,String])= {

    val (javaFiles,scalaFiles) = fileMap partition {
      case (k,_) => k contains ".java"
    }

    compileJava(javaFiles)
    val scalaInstances : Iterable[Any] = compileScala(scalaFiles)

    val result = scalaInstances find( x => x match{
        case model: DataModel => true
        case _ => false
      }
    )
    
    result match{

      case Some(x) => x match{

        case model : DataModel => dataModelJsonInterface.getJson(model)
        case _ => Json.toJson("Error")
      }
      case _ => Json.toJson("No DataModel found.")
    }


  }

  /*
   *    * For a given FQ classname, trick the resource finder into telling us the containing jar.
   *
   *
   * */
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
  def compileScala(files:Map[String,String]) : Iterable[Any]= { val result = files map {

    case(name,code)=>{

      play.api.Logger.info("Compiling Scala code.")

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
    
      val clazz = instantiateClass(name,getCodePackageName(code))

      clazz
    }
    
    }
    result
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
    val getDeclaredMethod : Method= new URLClassLoader(Array(rootURL)).getClass().getDeclaredMethod("getURLs")
    getDeclaredMethod.setAccessible(true);
    val result = getDeclaredMethod.invoke(ClassLoader.getSystemClassLoader()).asInstanceOf[Array[URL]]
    result
  }
  def instantiateClass(fileName : String, packageName: String) = {
    // Load and instantiate compiled class.
    val name = fileName.split('.')
    val runtimeMirror = universe.runtimeMirror(classLoader)
    //val clazz = classLoader.loadClass(packageName + "." + name(0)) 
    //val constructor = clazz.getConstructor()
    //constructor.setAccessible(true)
    //val instance = constructor.newInstance()
    val module = runtimeMirror.staticModule(packageName + "." + name(0))

    val obj = runtimeMirror.reflectModule(module)

    obj.instance
  }
  def getCodePackageName(code: String) = {
    (re findFirstIn code) match {    
      case Some(v) => (v.split(" "))(1).replaceFirst("\\n", "").replaceFirst(";","")   
      case None => play.api.Logger.error("Could not find package name."); ""
    }
  }
}
