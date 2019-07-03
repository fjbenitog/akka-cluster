package com.javi.cluster.bootstrap.server
import akka.actor.ActorSystem
import cats.effect.IO

object Backend {

  def start: IO[Unit] = {

    implicit val actorSystem: ActorSystem = ActorSystem("bankifitest-system")

    IO.pure()
  }

}

class Backend(implicit val actorSystem: ActorSystem){

}
