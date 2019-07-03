import sbt._
import Keys._

object Dependencies {

  object Cats {
    val core   = "org.typelevel" %% "cats-core"   % Versions.cats.main
    val effect = "org.typelevel" %% "cats-effect" % Versions.cats.effect

    lazy val All = Seq(core, effect)
  }

  object Akka {
    val actor  = "com.typesafe.akka" %% "akka-actor"      % Versions.akka.main
    val stream = "com.typesafe.akka" %% "akka-stream"     % Versions.akka.main
    val http   = "com.typesafe.akka" %% "akka-http"       % Versions.akka.http
    val json   = "de.heikoseeberger" %% "akka-http-circe" % Versions.akka.json

    object testkit {
      val common = "com.typesafe.akka" %% "akka-testkit"      % Versions.akka.main
      val http   = "com.typesafe.akka" %% "akka-http-testkit" % Versions.akka.http

      lazy val All = Seq(common, stream, http).map(_ % Test)
    }

    lazy val Common = Seq(actor, stream, http, json) ++ testkit.All
  }

  object Testing {

    object scalaTest {
      val scalaTest = "org.scalatest" %% "scalatest" % Versions.scalaTest

      lazy val Default = Seq(scalaTest).map(_ % Test)
    }

  }
  object Logging {

    val akka = "com.typesafe.akka" %% "akka-slf4j" % Versions.akka.main

    object slf4j {
      val api = "org.slf4j" % "slf4j-api"      % Versions.slf4j
      val jcl = "org.slf4j" % "jcl-over-slf4j" % Versions.slf4j

      lazy val All = Seq(api, jcl)
    }

    object log4j {
      val core  = "org.apache.logging.log4j" % "log4j-core"       % Versions.log4j
      val api   = "org.apache.logging.log4j" % "log4j-api"        % Versions.log4j
      val slf4j = "org.apache.logging.log4j" % "log4j-slf4j-impl" % Versions.log4j

      lazy val All = Seq(core, api, slf4j)
    }

    lazy val All = slf4j.All ++ log4j.All
  }

  lazy val akkaCluster = Def.settings {
    libraryDependencies ++= Seq(
      Akka.Common,
      Cats.All,
      Testing.scalaTest.Default,
      Logging.All,
      Seq(Logging.akka)
    ).flatten
  }
}