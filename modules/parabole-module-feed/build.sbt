name := "parabole-module-feed"

version := "1.0-SNAPSHOT"

PlayKeys.devSettings += ("play.http.router", "feed.Routes")

lazy val feed = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++=  Seq(
  javaJdbc,
  cache,
  javaWs
)
