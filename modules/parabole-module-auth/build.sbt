name := "parabole-module-auth"

version := "1.0-SNAPSHOT"

Common.settings

PlayKeys.devSettings += ("play.http.router", "auth.Routes")

lazy val auth = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Common.commonDependencies ++: Seq(
  javaJdbc,
  cache,
  javaWs
)
