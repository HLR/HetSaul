// scala style formatter
addSbtPlugin("com.github.jkinkead" % "sbt-scalariform" % "0.1.6")

// to generate dependency tree based upon the library dependencies
addSbtPlugin("com.gilt" % "sbt-dependency-graph-sugar" % "0.7.5-1")

// to automatically add header descriptions to files
addSbtPlugin("de.heikoseeberger" % "sbt-header" % "1.6.0")

// to bump the version numbers and automate the release process
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.3")

// to use git directly inside sbt as well as using it for versioning
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.5")

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.3")

// web plugins for the webapp
addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.6")
addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.3")
addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.7")
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-mocha" % "1.1.0")