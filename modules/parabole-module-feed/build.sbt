name := "parabole-module-feed"

version := "1.0-SNAPSHOT"

PlayKeys.devSettings += ("play.http.router", "feed.Routes")

lazy val feed = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++=  Seq(
  javaJdbc,
  javaWs,
  cache,
  javaWs,
  "com.itextpdf" % "itext-pdfa" % "5.5.6-1",
  "com.itextpdf.tool" % "xmlworker" % "5.5.6",
  "org.apache.xmlgraphics" % "batik-transcoder" % "1.8",
  "org.apache.xmlgraphics" % "batik-codec" % "1.8",
  "org.apache.jena" % "apache-jena-libs" % "3.0.1",
  "com.verhas" % "license3j" % "1.0.4",
  "org.webjars" % "bootstrap" % "3.3.5",
  "org.webjars" % "jquery" % "2.1.4",
  "com.google.inject.extensions" % "guice-multibindings" % "4.0",
  "com.orientechnologies" % "orientdb-graphdb" % "2.1.4",
  "org.apache.commons" % "commons-lang3" % "3.4",
  "org.apache.commons" % "commons-collections4" % "4.1",
  "org.apache.poi" % "poi" % "3.14",
  "org.apache.poi" % "poi-ooxml" % "3.14",
  "commons-codec" % "commons-codec" % "1.10",
  "commons-io" % "commons-io" % "2.5",
  "commons-pool" % "commons-pool" % "1.6",
  "commons-dbutils" % "commons-dbutils" % "1.6",
  "cglib" % "cglib" % "3.2.2",
  "org.json" % "json" % "20160212",
  "com.google.code.gson" % "gson" % "2.6.2",
  "be.objectify" %% "deadbolt-java" % "2.4.4",
  "net.sf.jsqlparser" % "jsqlparser" % "0.8.0"
)
