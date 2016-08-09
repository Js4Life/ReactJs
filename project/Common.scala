import sbt._
import Keys._
import play.sbt.PlayImport._
import play.sbt.routes.RoutesKeys.routesGenerator
import play.routes.compiler.InjectedRoutesGenerator

object Common {

    val settings: Seq[Setting[_]] = Seq(
        organization := "com.parabole.finance",
        routesGenerator := InjectedRoutesGenerator,
        logLevel := Level.Error,
        scalaVersion in ThisBuild := "2.11.7",
        scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked", "-language:reflectiveCalls"),
        resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
        resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
        resolvers += "Typesafe Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
    )
  
    val commonDependencies = Seq(
        javaCore,
        javaJdbc,
        javaWs,
        cache,
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
        "be.objectify" % "deadbolt-java_2.11" % "2.5.1"
    )    
}