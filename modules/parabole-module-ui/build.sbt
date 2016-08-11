name := "parabole-module-ui"

version := "1.0-SNAPSHOT"

Common.settings

PlayKeys.devSettings += ("play.http.router", "ui.Routes")



scalaVersion := "2.11.7"

libraryDependencies ++= Common.commonDependencies ++: Seq(
  javaJdbc,
  cache,
  javaWs
)

val authTwo = RootProject(file("./modules/parabole-module-auth"))

lazy val ui = (project in file(".")).enablePlugins(PlayJava)
  .aggregate(authTwo)
  .dependsOn(authTwo)


