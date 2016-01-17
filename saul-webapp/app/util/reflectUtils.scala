package util

object reflectUtils {

  val re = """package\s(.*)\s""".r

  def getCodePackageName(code: String) = {
    (re findFirstIn code) match {
      case Some(v) => (v.split(" "))(1).replaceFirst("\\n", "").replaceFirst(";", "")
      case None => play.api.Logger.error("Could not find package name."); ""
    }
  }

  /*
     *    * For a given FQ classname,
     *    trick the resource finder into telling us the containing jar.
     *
     * */
  def classPathOfClass(className: String) = {
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

}
