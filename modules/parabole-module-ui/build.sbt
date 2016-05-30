name := "parabole-module-ui"

version := "1.0-SNAPSHOT"

Common.settings

PlayKeys.devSettings += ("play.http.router", "ui.Routes")

lazy val ui = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Common.commonDependencies ++: Seq(
  javaJdbc,
  cache,
  javaWs
)
