package util

object ReflectUtils {

  //Example input: package somePackageName;
  val re = """package\s(.*)\s""".r

  def getCodePackageName(code: String) = {
    (re findFirstIn code) match {
      case Some(v) => (v.split(" "))(1).replaceFirst("\\n", "").replaceFirst(";", "")
      case None => play.api.Logger.error("Could not find package name."); ""
    }
  }

  def classPathOfClass(className: String) = {
    val resource = className.split('.').mkString("/", "/", ".class")
    var path = ""
    try{
      path = getClass.getResource(resource).getPath
    } catch {
      case e: Exception => play.api.Logger.info(resource + " not found.\n")
    }
    if (path.indexOf("file:") >= 0) {
      //using path after 5 characters which excludes "file:"
      val indexOfFile = path.indexOf("file:") + 5
      val indexOfSeparator = path.lastIndexOf('!')
      List(path.substring(indexOfFile, indexOfSeparator))
    } else {
      require(path.endsWith(resource))
      List(path.substring(0, path.length - resource.length + 1))
    }
  }

}
