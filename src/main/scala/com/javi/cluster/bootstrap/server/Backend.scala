package com.javi.cluster.bootstrap.server
import akka.actor.ActorSystem
import cats.effect.IO
import com.typesafe.config.Config

object Backend {

  def start(config: Config): IO[Backend] = {
    implicit val actorSystem: ActorSystem = ActorSystem("akka-cluster-system",config)
    IO(new Backend())
  }

}

class Backend(implicit val actorSystem: ActorSystem) {

  val clusterListener = actorSystem.actorOf(SimpleClusterListener.props)
}
