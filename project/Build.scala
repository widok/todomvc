import sbt.Keys._
import sbt._
import scala.scalajs.sbtplugin.ScalaJSPlugin._

object Build extends sbt.Build {
  val buildOrganisation = "org.widok"
  val buildVersion = "0.1.3"
  val buildScalaVersion = "2.11.2"
  val buildScalaOptions = Seq(
    "-unchecked", "-deprecation",
    "-encoding", "utf8"
  )

  lazy val main = Project(id = "todomvc", base = file("."))
    .settings(scalaJSSettings: _*)
    .settings(
      resolvers += "Sonatype Nexus Releases" at "https://oss.sonatype.org/content/repositories/releases",
      libraryDependencies ++= Seq(
        "io.github.widok" %%% "widok" % "0.1.3"
      ),
      organization := buildOrganisation,
      version := buildVersion,
      scalaVersion := buildScalaVersion,
      scalacOptions := buildScalaOptions,
      ScalaJSKeys.persistLauncher := true
    )
}
