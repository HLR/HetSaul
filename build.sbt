lazy val root = (project in file(".")).
  aggregate(saulCore, saulExamples)

lazy val commonSettings = Seq(
  organization := "edu.illinois.cs.cogcomp",
  name := "saul-project",
  version := "0.1",
  scalaVersion := "2.11.7",
  resolvers ++= Seq(
    Resolver.mavenLocal,
    "CogcompSoftware" at "http://cogcomp.cs.illinois.edu/m2repo/"
  ),
  libraryDependencies ++= Seq(
    "com.gurobi" % "gurobi" % "6.0",
    "org.apache.commons" % "commons-math3" % "3.0"
  )
)

lazy val saulCore = (project in file("saul-core")).
  settings(commonSettings: _*).
  settings(
    name := "saul",
    libraryDependencies ++= Seq(
      "de.bwaldvogel" % "liblinear" % "1.94",
      "net.sf.squirrel-sql.thirdparty.non-maven" % "java-cup" % "11a",
      "org.dmilne" % "weka-wrapper" % "0.0.1"
    )
  )

lazy val saulExamples = (project in file("saul-examples")).
  settings(commonSettings: _*).
  settings(
    name := "saul-examples",
    libraryDependencies ++= Seq(
      "edu.illinois.cs.cogcomp" % "illinois-core-utilities" % "1.2.12-SNAPSHOT",
      "edu.illinois.cs.cogcomp" % "illinois-nlp-pipeline" % "0.1.6" exclude("edu.illinois.cs.cogcomp", "LBJava"),
      "edu.illinois.cs.cogcomp" % "illinois-caching-curator" % "2.1.1",
      "edu.illinois.cs.cogcomp" % "edison" % "1.7.9" exclude("edu.illinois.cs.cogcomp", "LBJava"),
      "org.scalatest" % "scalatest_2.11" % "2.2.4"
    )
  ).dependsOn(saulCore).aggregate(saulCore)
