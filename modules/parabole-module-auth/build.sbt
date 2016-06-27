name := "parabole-module-auth"

version := "1.0-SNAPSHOT"

Common.settings


scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs
)