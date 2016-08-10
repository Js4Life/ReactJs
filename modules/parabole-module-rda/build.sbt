name := "parabole-module-rda"

Common.settings

PlayKeys.devSettings += ("play.http.router", "rda.Routes")

libraryDependencies ++= Common.commonDependencies ++: Seq(
  "com.itextpdf" % "itext-pdfa" % "5.5.6-1",
  "com.itextpdf.tool" % "xmlworker" % "5.5.6",
  "org.apache.xmlgraphics" % "batik-transcoder" % "1.8",
  "org.apache.xmlgraphics" % "batik-codec" % "1.8"
)

