import sbt._
import sbt.Keys._
import org.scalajs.sbtplugin._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

object Build extends sbt.Build {
  val buildOrganisation = "org.widok"
  val buildVersion = "0.1.4"
  val buildScalaVersion = "2.11.4"
  val buildScalaOptions = Seq(
    "-unchecked", "-deprecation",
    "-encoding", "utf8"
  )

  lazy val main = Project(id = "todomvc", base = file("."))
    .enablePlugins(ScalaJSPlugin)
    .settings(
      resolvers += "Sonatype Nexus Releases" at "https://oss.sonatype.org/content/repositories/releases",
      libraryDependencies ++= Seq(
        "io.github.widok" %%% "widok" % "0.1.4-SNAPSHOT"
      ),
      organization := buildOrganisation,
      version := buildVersion,
      scalaVersion := buildScalaVersion,
      scalacOptions := buildScalaOptions,
      persistLauncher := true
    )
}
