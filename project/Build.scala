import sbt.Keys._
import sbt._
import scala.scalajs.sbtplugin.ScalaJSPlugin._

object Build extends sbt.Build {
  val buildOrganisation = "org.widok"
  val buildVersion = "0.1.1"
  val buildScalaVersion = "2.11.2"
  val buildScalaOptions = Seq(
    "-unchecked", "-deprecation",
    "-encoding", "utf8"
  )

  lazy val main = Project(id = "todomvc", base = file("."))
    .settings(scalaJSSettings: _*)
    .settings(
      resolvers += "Sonatype Nexus Staging" at "https://oss.sonatype.org/service/local/staging/deploy/maven2",
      libraryDependencies ++= Seq(
        "org.widok" %%% "widok" % "0.1.1"
      ),
      organization := buildOrganisation,
      version := buildVersion,
      scalaVersion := buildScalaVersion,
      scalacOptions := buildScalaOptions,
      ScalaJSKeys.persistLauncher := true
    )
}
