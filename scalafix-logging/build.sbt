lazy val V = _root_.scalafix.sbt.BuildInfo
inThisBuild(
  List(
    organization := "net.runne",
    homepage := Some(url("https://github.com/ennru/scalafix-logging")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "ennru",
        "Enno",
        "",
        url("https://dn.se")
      )
    ),
    scalaVersion := V.scala212,
    addCompilerPlugin(scalafixSemanticdb),
    scalacOptions ++= List("-Yrangepos")
  )
)

publish / skip := true

lazy val rules = project.settings(
  moduleName := "scalafix-logging",
  libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion
)

lazy val input = project.settings(
  publish / skip := true,
  libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.29",
  libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
)

lazy val output = project.settings(
  publish / skip := true,
  libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.29",
  libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
)

lazy val tests = project
  .settings(
    publish / skip := true,
    libraryDependencies += "ch.epfl.scala" % "scalafix-testkit" % V.scalafixVersion % Test cross CrossVersion.full,
    scalafixTestkitOutputSourceDirectories := (output / Compile / sourceDirectories).value,
    scalafixTestkitInputSourceDirectories := (input / Compile / sourceDirectories).value,
    scalafixTestkitInputClasspath := (input / Compile / fullClasspath).value
  )
  .dependsOn(rules)
  .enablePlugins(ScalafixTestkitPlugin)
