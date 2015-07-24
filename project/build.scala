import sbt._
import Keys._
import com.typesafe.sbt.packager.archetypes._
import scalaprops.ScalapropsPlugin.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.sbtplugin.cross.CrossProject

object MyLordBuild extends Build {
  import Dependencies._

  val mylord = CrossProject("my-lord", file(""), CrossType.Full)
    .settings(
      scalaVersion := "2.11.7"
    )

  lazy val mylordJs = mylord.js.in(file("mylord-js")).settings(
    resolvers ++= Seq(
      "amateras-repo" at "http://amateras.sourceforge.jp/mvn-snapshot/"
    ),
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % Version.scalaJsDom,
      "com.lihaoyi" %%% "autowire"      % Version.autowire,
      "com.github.japgolly.scalajs-react" %%% "ext-scalaz71"  % Version.scalaJsReact,
      "com.scalawarrior" %%% "scalajs-ace" % Version.ace
    ),
    emitSourceMaps := false,
    persistLauncher := true,
    jsDependencies ++= Seq(
      RuntimeDOM,
      "org.webjars" % "react" % "0.12.2" / "react-with-addons.js" commonJSName "React" minified "react-with-addons.min.js",
      "org.webjars" % "ace" % "01.08.2014" / "src-noconflict/ace.js",
      "org.webjars" % "ace" % "01.08.2014" / "src-noconflict/theme-github.js" dependsOn "src-noconflict/ace.js",
      "org.webjars" % "ace" % "01.08.2014" / "src-noconflict/mode-scala.js" dependsOn "src-noconflict/ace.js"
    ),
    artifactPath in (Compile, fastOptJS) :=
      ((baseDirectory in mylordJvm).value / ("src/main/resources/static/js/" + (moduleName in fastOptJS).value + "-opt.js")),
    artifactPath in (Compile, packageScalaJSLauncher) :=
      ((baseDirectory in mylordJvm).value / ("src/main/resources/static/js/" + (moduleName in fastOptJS).value + "-launcher.js")),
    artifactPath in (Compile, packageJSDependencies) :=
      ((baseDirectory in mylordJvm).value / ("src/main/resources/static/js/" + (moduleName in fastOptJS).value + "-jsdeps.js"))
  )

  lazy val mylordJvm = mylord.jvm.in(file("mylord-jvm")).settings(
    scalapropsSettings,
    resolvers ++= Seq(
      Opts.resolver.sonatypeReleases,
      "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
      "tpolecat" at "http://dl.bintray.com/tpolecat/maven"
    ),
    mainClass := Some("mylord.MyLordApp"),
    libraryDependencies ++= Seq(
      http4sCore,
      http4sDSL,
      http4sBlaze,
      http4sBlazeClient,
      http4sArgonaut,
      nscalaTime,
      doobiePostgresql,
      config,
      logbackClassic,
      scalatest % "test"
    ),
    scalapropsVersion := Version.scalaprops
  ).enablePlugins(JavaAppPackaging)

  object Dependencies {

    object Version {
      val http4s = "0.8.4"
      val nscalaTime = "2.0.0"
      val doobie = "0.2.2"
      val config = "1.3.0"
      val logbackClassic = "1.1.2"
      val scalaprops = "0.1.11"
      val scalaJsDom = "0.8.1"
      val autowire = "0.2.5"
      val scalaJsReact ="0.9.1"
      val ace = "0.0.1-SNAPSHOT"
    }

    val http4sCore  = "org.http4s" %% "http4s-core"    % Version.http4s
    val http4sDSL   = "org.http4s" %% "http4s-dsl"     % Version.http4s
    val http4sBlaze = "org.http4s" %% "http4s-blazeserver"   % Version.http4s
    val http4sBlazeClient = "org.http4s" %% "http4s-blazeclient"   % Version.http4s
    val http4sArgonaut = "org.http4s" %% "http4s-argonaut"   % Version.http4s
    val scalatest = "org.scalatest" %% "scalatest" % "2.2.4"
    val nscalaTime = "com.github.nscala-time" %% "nscala-time" % Version.nscalaTime
    val doobiePostgresql = "org.tpolecat" %% "doobie-contrib-postgresql" % Version.doobie
    val config = "com.typesafe" % "config" % Version.config
    val logbackClassic = "ch.qos.logback" % "logback-classic" % Version.logbackClassic
  }
}
