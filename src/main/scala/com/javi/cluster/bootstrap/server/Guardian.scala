package com.javi.cluster.bootstrap.server

import akka.actor._
import akka.cluster.Cluster
import com.javi.cluster.bootstrap.server.pubsub.WorkerActor
import com.javi.cluster.bootstrap.server.sharding.CounterCoordinator
import com.javi.cluster.bootstrap.server.singleton.SchedulerActor

object Guardian {
  def props = Props(new Guardian)
}

class Guardian extends Actor {
  private[this] val TopicName = "Executor-task"

  private[this] val cluster = Cluster(context.system)

  private[this] val system = context.system

  override def preStart(): Unit = {
    system.actorOf(SimpleClusterListener.props(cluster))

    SchedulerActor.start(TopicName)

    val counterCoordinator = system.actorOf(CounterCoordinator.props)

    system.actorOf(WorkerActor.props(TopicName, counterCoordinator))
  }

  override def receive: Receive = {
    case _ =>
  }
}
