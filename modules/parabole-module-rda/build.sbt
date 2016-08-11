name := "parabole-module-rda"

Common.settings

PlayKeys.devSettings += ("play.http.router", "rda.Routes")

libraryDependencies ++= Common.commonDependencies ++: Seq(
  "com.itextpdf" % "itext-pdfa" % "5.5.6-1",
  "com.itextpdf.tool" % "xmlworker" % "5.5.6",
  "org.apache.xmlgraphics" % "batik-transcoder" % "1.8",
  "org.apache.xmlgraphics" % "batik-codec" % "1.8"
)

val authTwo = RootProject(file("./modules/parabole-module-auth"))


lazy val rda = (project in file(".")).enablePlugins(PlayJava)
  .aggregate(authTwo)
  .dependsOn(authTwo)
