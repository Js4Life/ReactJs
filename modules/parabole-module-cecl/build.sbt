name := "parabole-module-cecl"

Common.settings

PlayKeys.devSettings += ("play.http.router", "cecl.Routes")

libraryDependencies ++= Common.commonDependencies ++: Seq(
  "com.itextpdf" % "itext-pdfa" % "5.5.6-1",
  "com.itextpdf.tool" % "xmlworker" % "5.5.6",
  "org.apache.xmlgraphics" % "batik-transcoder" % "1.8",
  "org.apache.xmlgraphics" % "batik-codec" % "1.8",
  "org.apache.jena" % "apache-jena-libs" % "3.0.1"
)

val feed = RootProject(file("./modules/parabole-module-feed"))
val auth = RootProject(file("./modules/parabole-module-auth"))


lazy val cecl = (project in file(".")).enablePlugins(PlayJava)
  .aggregate(feed, auth)
  .dependsOn(feed, auth)

