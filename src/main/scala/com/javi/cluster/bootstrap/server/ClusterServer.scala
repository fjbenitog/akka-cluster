package com.javi.cluster.bootstrap.server
import akka.actor.ActorSystem
import akka.cluster.Cluster
import cats.effect.IO
import com.typesafe.config.Config

object ClusterServer {

  def start(config: Config): IO[ClusterServer] = {
    implicit val actorSystem: ActorSystem = ActorSystem("akka-cluster-system",config)
    IO(new ClusterServer())
  }

}

class ClusterServer(implicit val actorSystem: ActorSystem) {
  val cluster = Cluster(actorSystem)
  val clusterListener = actorSystem.actorOf(SimpleClusterListener.props(cluster))
}
