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
    "edu.illinois.cs.cogcomp" % "illinois-core-utilities" % "1.3.0",
    "com.gurobi" % "gurobi" % "6.0",
    "org.apache.commons" % "commons-math3" % "3.0",
    "org.scalatest" % "scalatest_2.11" % "2.2.4"
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
    javaOptions += "-Xmx6g",
    libraryDependencies ++= Seq(
      // slf4j is required by both annotators (Curator, Pipeline)
      "org.slf4j" % "slf4j-simple" % "1.7.7",
      "edu.illinois.cs.cogcomp" % "illinois-nlp-pipeline" % "0.1.9" exclude("edu.illinois.cs.cogcomp", "LBJava"),
      "edu.illinois.cs.cogcomp" % "illinois-curator" % "3.1.1",
      "edu.illinois.cs.cogcomp" % "edison" % "1.7.9" exclude("edu.illinois.cs.cogcomp", "LBJava")
    )
  ).dependsOn(saulCore).aggregate(saulCore)
