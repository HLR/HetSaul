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
    "edu.illinois.cs.cogcomp" % "illinois-core-utilities" % "1.2.12-SNAPSHOT",
    "edu.illinois.cs.cogcomp" % "illinois-caching-curator" % "2.1.1",
    "edu.illinois.cs.cogcomp" % "illinois-nlp-pipeline" % "0.1.6",
    "org.apache.commons" % "commons-math3" % "3.0"
  )
)

lazy val saulCore = (project in file("saul-core")).
  settings(commonSettings: _*).
  settings(
    name := "saul",
    libraryDependencies ++= Seq(
      "de.bwaldvogel" % "liblinear" % "1.94"
    )
  )

lazy val saulExamples = (project in file("saul-examples")).
  settings(commonSettings: _*).
  settings(
    name := "saulexamplespackage",
    libraryDependencies ++= Seq(
    )
  ).dependsOn(saulCore).aggregate(saulCore)
