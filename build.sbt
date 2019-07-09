import Dependencies._
import com.typesafe.sbt.packager.docker.DockerChmodType

lazy val akkaVersion = "2.5.22"

lazy val akkaCluster = (project in file("."))
  .enablePlugins(DockerComposePlugin, RevolverPlugin, JavaAppPackaging)
  .settings(
    name := "akka-cluster",
    dockerImageCreationTask := (publishLocal in Docker).value,
    dockerBaseImage := "openjdk:8-jre-stretch",
    dockerUpdateLatest := true,
//    dockerExposedPorts := Seq(2552),
    javaOptions in Universal ++= Seq(
      "-Dconfig.file=/config/application.conf",
      "-Dlog4j.configurationFile=/config/log4j2.xml"
    )
  )
  .settings(Dependencies.akkaCluster)

// === Command Aliases

addCommandAlias(
  "launchLocal",
  Seq(
    "docker:publishLocal",
    "dockerComposeUp"
  ).mkString(";", ";", "")
)