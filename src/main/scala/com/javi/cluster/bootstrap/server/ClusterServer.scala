package com.javi.cluster.bootstrap.server
import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement
import cats.effect.IO

object ClusterServer {

  def start: IO[ClusterServer] = {
    implicit val actorSystem: ActorSystem = ActorSystem("akka-cluster-system")
    IO(new ClusterServer())
  }

}

class ClusterServer(implicit val actorSystem: ActorSystem) {
  AkkaManagement(actorSystem).start()
  ClusterBootstrap(actorSystem).start()
  val cluster = Cluster(actorSystem)
  val clusterListener = actorSystem.actorOf(SimpleClusterListener.props(cluster))
}
