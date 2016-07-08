import play.sbt.PlayJava
import play.sbt.routes.RoutesKeys._
import sbt.RootProject

name := "parabole-enterprise-scaffolding"

lazy val root = (project in file(".")).enablePlugins(PlayJava)
                .aggregate(ccar, rda, ui, auth, cecl)
                .dependsOn(ccar, rda, ui, auth, cecl)


lazy val auth = (project in file("modules/parabole-module-auth")).enablePlugins(PlayScala)
lazy val ccar = (project in file("modules/parabole-module-ccar")).enablePlugins(PlayScala)
lazy val rda = (project in file("modules/parabole-module-rda")).enablePlugins(PlayScala)
lazy val ui = (project in file("modules/parabole-module-ui")).enablePlugins(PlayScala)
lazy val cecl = (project in file("modules/parabole-module-cecl")).enablePlugins(PlayScala)
//lazy val feed = (project in file("modules/parabole-module-feed")).enablePlugins(PlayScala)

routesGenerator := InjectedRoutesGenerator

Common.settings

libraryDependencies ++= Common.commonDependencies