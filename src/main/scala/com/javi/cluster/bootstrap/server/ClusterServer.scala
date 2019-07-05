package com.javi.cluster.bootstrap.server
import akka.actor._
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement

import cats.implicits._
import cats.effect._

object ClusterServer {

  def start: IO[Unit] = {
    implicit val actorSystem: ActorSystem = ActorSystem("akka-cluster-system")
    startAkkaManagement *> startClusterBootstrap *> IO(new ClusterServer)
  }
  private def startAkkaManagement(implicit actorSystem: ActorSystem) =
    IO(AkkaManagement(actorSystem).start()).void

  private def startClusterBootstrap(implicit actorSystem: ActorSystem) =
    IO(ClusterBootstrap(actorSystem).start())

}

class ClusterServer(implicit actorSystem: ActorSystem) {
  actorSystem.actorOf(Guardian.props)

}
