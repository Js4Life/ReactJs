name := "parabole-module-ccar"

Common.settings

PlayKeys.devSettings += ("play.http.router", "ccar.Routes")

libraryDependencies ++= Common.commonDependencies ++: Seq(
  "org.apache.jena" % "apache-jena-libs" % "3.0.1",
  "log4j" % "log4j" % "1.2.17"
)


val auth = RootProject(file("./modules/parabole-module-auth"))


lazy val ccar = (project in file(".")).enablePlugins(PlayJava)
  .aggregate(auth)
  .dependsOn(auth)