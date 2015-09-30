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
  )
)

lazy val saulCore = (project in file("saul-core")).
  settings(commonSettings: _*).
  settings(
    name := "saul",
    libraryDependencies ++= Seq(
      "edu.illinois.cs.cogcomp" % "illinois-caching-curator" % "2.1.1",
      "edu.illinois.cs.cogcomp" % "illinois-nlp-pipeline" % "0.1.6",
      "edu.illinois.cs.cogcomp" % "illinois-core-utilities" % "1.2.12-SNAPSHOT",

      // external
      "com.typesafe.play" %% "anorm" % "2.3.6",
      "com.h2database" % "h2" % "1.3.162",
      // Change this to another test framework if you prefer
      "org.scalatest" %% "scalatest" % "2.1.6" % "test",
      "nz.ac.waikato.cms.weka" % "weka-stable" % "3.6.10",
      "de.bwaldvogel" % "liblinear" % "1.94",
      "org.apache.commons" % "commons-math3"  % "3.5"
    )
  )

lazy val saulExamples = (project in file("saul-examples")).
  settings(commonSettings: _*).
  settings(
    name := "saulexamplespackage",
    libraryDependencies ++= Seq(
      "org.apache.commons" % "commons-math3" % "3.0",
      "edu.illinois.cs.cogcomp" % "illinois-ner" % "2.6"
    )
  ).dependsOn(saulCore).aggregate(saulCore)