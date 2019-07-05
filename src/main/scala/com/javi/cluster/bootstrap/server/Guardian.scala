package com.javi.cluster.bootstrap.server
import akka.actor._
import akka.cluster.Cluster

import com.javi.cluster.bootstrap.server.singleton.SchedulerActor

object Guardian {

  def props = Props(new Guardian)
}

class Guardian extends Actor {

  private[this] val cluster = Cluster(context.system)

  private[this] val system = context.system

  system.actorOf(SimpleClusterListener.props(cluster))

  private[this] val schedulerActor = SchedulerActor.start

  override def receive: Receive = {
    case _ =>
  }
}
