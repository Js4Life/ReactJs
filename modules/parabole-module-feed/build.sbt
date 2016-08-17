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
  "net.sf.jsqlparser" % "jsqlparser" % "0.8.0",
  "org.json" % "json" % "20151123",
  "org.apache.jena" % "apache-jena-libs" % "3.0.0",
  "org.apache.commons" % "commons-csv" % "1.2",
  "org.apache.commons" % "commons-lang3" % "3.4",
  "commons-lang" % "commons-lang" % "2.6",
  "commons-pool" % "commons-pool" % "1.6",
  "commons-io" % "commons-io" % "2.4",
  "commons-collections" % "commons-collections" % "3.2.1",
  "org.apache.poi" % "poi" % "3.12",
  "org.apache.poi" % "poi-ooxml" % "3.12",
  "org.apache.commons" % "commons-collections4" % "4.0",
  "com.google.code.gson" % "gson" % "1.7.1",
  "org.modelmapper" % "modelmapper" % "0.7.4",
  "javax.mail" % "mail" % "1.4.1",
  "org.apache.commons" % "commons-email" % "1.3.1",
  "log4j" % "log4j" % "1.2.15",
  "fr.xebia.extras" % "selma-processor" % "0.14",
  "com.worldpay.api.client" % "worldpay-client-core" % "0.0.1",
  "com.google.apis" % "google-api-services-calendar" % "v3-rev35-1.14.1-beta",
  "com.google.apis" % "google-api-services-calendar" % "v3-rev35-1.14.1-beta",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.3.0",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.3.0" classifier "models",
  "org.slf4j" % "slf4j-api" % "1.7.21",
  "org.slf4j" % "slf4j-simple" % "1.7.7",
  "edu.stanford.nlp" % "stanford-corenlp" % "1.2.0",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0" classifier "models",
  "edu.stanford.nlp" % "stanford-parser" % "3.6.0",
  "edu.washington.cs.knowitall.stanford-corenlp" % "stanford-parse-models" % "1.3.5",
  "edu.stanford.nlp" % "stanford-parser" % "2.0.2",
  "org.apache.pdfbox" % "fontbox" % "2.0.1",
  "org.apache.pdfbox" % "pdfbox" % "2.0.1"
)
