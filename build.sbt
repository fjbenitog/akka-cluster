import Dependencies._
import com.typesafe.sbt.packager.docker.DockerChmodType

lazy val akkaVersion = "2.5.22"

lazy val akkaCluster = (project in file("."))
  .enablePlugins(DockerComposePlugin,RevolverPlugin, JavaAppPackaging)
  .settings(
    name := "akka-cluster",
    dockerImageCreationTask := (publishLocal in Docker).value,
    dockerBaseImage := "openjdk:8-jre-stretch",
    dockerUpdateLatest := true,
//    dockerExposedPorts := Seq(8080,9095),
    dockerAdditionalPermissions += (DockerChmodType.UserGroupWriteExecute, s"${(defaultLinuxInstallLocation in Docker).value}/sigar-loader"),
  )
  .settings(Dependencies.akkaCluster)