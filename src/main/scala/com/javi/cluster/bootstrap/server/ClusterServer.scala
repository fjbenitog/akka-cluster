package com.javi.cluster.bootstrap.server
import akka.actor.ActorSystem
import akka.cluster.Cluster
import cats.effect.IO

object ClusterServer {

  def start: IO[ClusterServer] = {
    implicit val actorSystem: ActorSystem = ActorSystem("akka-cluster-system")
    IO(new ClusterServer())
  }

}

class ClusterServer(implicit val actorSystem: ActorSystem) {
  val cluster = Cluster(actorSystem)
  val clusterListener = actorSystem.actorOf(SimpleClusterListener.props(cluster))
}
