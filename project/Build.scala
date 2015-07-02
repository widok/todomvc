import sbt._
import sbt.Keys._
import org.scalajs.sbtplugin._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.core.tools.sem._

object Build extends sbt.Build {
  val buildOrganisation = "org.widok"
  val buildVersion = "0.2.2"
  val buildScalaVersion = "2.11.6"
  val buildScalaOptions = Seq(
    "-unchecked"
  , "-deprecation"
  , "-encoding", "utf8"
  )

  lazy val main = Project(id = "todomvc", base = file("."))
    .enablePlugins(ScalaJSPlugin)
    .settings(
      libraryDependencies ++= Seq(
        "io.github.widok" %%% "widok" % "0.2.2"
      )
    , organization := buildOrganisation
    , version := buildVersion
    , scalaVersion := buildScalaVersion
    , scalacOptions := buildScalaOptions
    , persistLauncher := true
    )
}
