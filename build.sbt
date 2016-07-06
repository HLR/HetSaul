import de.heikoseeberger.sbtheader.HeaderPattern

val cogcompNLPVersion = "3.0.40"
val cogcompPipelineVersion = "0.1.16"

lazy val headerMsg =  """/** This software is released under the University of Illinois/Research and Academic Use License. See
                        |  * the LICENSE file in the root folder for details. Copyright (c) 2016
                        |  *
                        |  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
                        |  * http://cogcomp.cs.illinois.edu/
                        |  */
                        |""".stripMargin

lazy val root = (project in file(".")).
  aggregate(saulCore, saulExamples).
  enablePlugins(AutomateHeaderPlugin)

lazy val commonSettings = Seq(
  organization := "edu.illinois.cs.cogcomp",
  name := "saul-project",
  version := "0.4",
  scalaVersion := "2.11.7",
  resolvers ++= Seq(
    Resolver.mavenLocal,
    "CogcompSoftware" at "http://cogcomp.cs.illinois.edu/m2repo/"
  ),
  javaOptions ++= List("-Xmx11g"),
  libraryDependencies ++= Seq(
    "edu.illinois.cs.cogcomp" % "LBJava" % "1.2.20" withSources,
    "edu.illinois.cs.cogcomp" % "illinois-core-utilities" % cogcompNLPVersion withSources,
    "com.gurobi" % "gurobi" % "6.0",
    "org.apache.commons" % "commons-math3" % "3.0",
    "org.scalatest" % "scalatest_2.11" % "2.2.4",
    "ch.qos.logback" % "logback-classic" % "1.1.7"
  ),
  fork := true,
  publishTo := Some(Resolver.sftp("CogcompSoftwareRepo", "bilbo.cs.illinois.edu", "/mounts/bilbo/disks/0/www/cogcomp/html/m2repo/")),
  isSnapshot := true,
  headers := Map(
    "scala" -> (HeaderPattern.cStyleBlockComment, headerMsg),
    "java" -> (HeaderPattern.cStyleBlockComment, headerMsg)
  )
)

lazy val saulCore = (project in file("saul-core")).
  settings(commonSettings: _*).
  settings(
    name := "saul",
    libraryDependencies ++= Seq(
      "com.typesafe.play" % "play_2.11" % "2.4.3"
    )
  ).enablePlugins(AutomateHeaderPlugin)

lazy val saulExamples = (project in file("saul-examples")).
  settings(commonSettings: _*).
  settings(
    name := "saul-examples",
    libraryDependencies ++= Seq(
      "edu.illinois.cs.cogcomp" % "illinois-nlp-pipeline" % cogcompPipelineVersion withSources,
      "edu.illinois.cs.cogcomp" % "illinois-curator" % cogcompNLPVersion,
      "edu.illinois.cs.cogcomp" % "illinois-edison" % cogcompNLPVersion,
      "edu.illinois.cs.cogcomp" % "illinois-nlp-readers" % "0.0.2-SNAPSHOT",
      "edu.illinois.cs.cogcomp" % "saul-pos-tagger-models" % "1.0",
      "edu.illinois.cs.cogcomp" % "saul-er-models" % "1.3",
      "edu.illinois.cs.cogcomp" % "saul-srl-models" % "1.1"
    )
  ).dependsOn(saulCore).aggregate(saulCore).
  enablePlugins(AutomateHeaderPlugin)

lazy val saulWebapp = (project in file("saul-webapp")).
  enablePlugins(PlayScala).
  settings(commonSettings: _*).
  settings(
    name := "saul-webapp",
    libraryDependencies ++= Seq(
      "org.webjars" %% "webjars-play" % "2.4.0-1",
      "org.webjars" % "bootstrap" % "3.3.6",
      "org.webjars.bower" % "tether-shepherd" % "1.1.3",
      "org.webjars" % "ace" % "1.2.2",
      "org.webjars" % "sigma.js" % "1.0.3",
      "org.webjars" % "d3js" % "3.5.16",
      "org.scala-lang" % "scala-compiler" % scalaVersion.value,
      jdbc,
      cache,
      ws,
      specs2 % Test
    ),
    resolvers ++= Seq("scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"),
    routesGenerator := InjectedRoutesGenerator
  ).dependsOn(saulExamples).aggregate(saulExamples).
  enablePlugins(AutomateHeaderPlugin)