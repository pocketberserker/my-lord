import sbt._
import Keys._
import com.typesafe.sbt.packager.archetypes._
import scalaprops.ScalapropsPlugin.autoImport._

object MyLordBuild extends Build {
  import Dependencies._

  lazy val buildSettings = Seq(
    scalapropsSettings
  ).flatten ++ Seq(
    scalaVersion := "2.11.6",
    resolvers ++= Seq(
      Opts.resolver.sonatypeReleases,
      "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
      "tpolecat" at "http://dl.bintray.com/tpolecat/maven"
    ),
    libraryDependencies ++= Seq(
      http4sCore,
      http4sDSL,
      http4sBlaze,
      http4sArgonaut,
      nscalaTime,
      doobiePostgresql,
      config,
      logbackClassic,
      scalatest % "test"
    ),
    scalapropsVersion := Version.scalaprops
  )

  lazy val myLord = Project(
    id = "my-lord-server",
    base = file("server"),
    settings = buildSettings
  ).enablePlugins(JavaAppPackaging)

  object Dependencies {

    object Version {
      val http4s = "0.8.4"
      val nscalaTime = "2.0.0"
      val doobie = "0.2.2"
      val config = "1.3.0"
      val logbackClassic = "1.1.2"
      val scalaprops = "0.1.10"
    }

    val http4sCore  = "org.http4s" %% "http4s-core"    % Version.http4s
    val http4sDSL   = "org.http4s" %% "http4s-dsl"     % Version.http4s
    val http4sBlaze = "org.http4s" %% "http4s-blazeserver"   % Version.http4s
    val http4sArgonaut = "org.http4s" %% "http4s-argonaut"   % Version.http4s
    val scalatest = "org.scalatest" %% "scalatest" % "2.2.4"
    val nscalaTime = "com.github.nscala-time" %% "nscala-time" % Version.nscalaTime
    val doobiePostgresql = "org.tpolecat" %% "doobie-contrib-postgresql" % Version.doobie
    val config = "com.typesafe" % "config" % Version.config
    val logbackClassic = "ch.qos.logback" % "logback-classic" % Version.logbackClassic
  }
}
