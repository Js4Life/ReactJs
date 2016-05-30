import play.sbt.PlayJava
import play.sbt.routes.RoutesKeys._
import sbt.RootProject

name := "parabole-platform"

lazy val root = (project in file(".")).enablePlugins(PlayJava)
                .aggregate(ccar, rda, ui)
                .dependsOn(ccar, rda, ui)

lazy val ccar = (project in file("modules/parabole-module-ccar")).enablePlugins(PlayScala)
lazy val rda = (project in file("modules/parabole-module-rda")).enablePlugins(PlayScala)
lazy val ui = (project in file("modules/parabole-module-ui")).enablePlugins(PlayScala)

routesGenerator := InjectedRoutesGenerator

Common.settings

libraryDependencies ++= Common.commonDependencies