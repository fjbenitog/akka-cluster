package com.javi.cluster.bootstrap.server.singleton
import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, PoisonPill, Props}
import akka.cluster.singleton._
import com.javi.cluster.bootstrap.server.pubsub.{Publisher, Task}

import scala.concurrent.duration._

case object ExecuteTask

object SchedulerActor {
  def props(topic: String) = Props(new SchedulerActor(topic))

  def start(topic: String)(implicit contextParent: ActorContext): ActorRef = {

    val singletonProps = ClusterSingletonManager.props(
      singletonProps = SchedulerActor.props(topic),
      terminationMessage = PoisonPill,
      settings = ClusterSingletonManagerSettings(contextParent.system)
    )

    contextParent.actorOf(singletonProps, name = "Scheduler-Tasks")

    contextParent.actorOf(
      ClusterSingletonProxy.props(
        contextParent.self.path.toStringWithoutAddress,
        ClusterSingletonProxySettings(contextParent.system)
      )
    )
  }
}

class SchedulerActor(topic: String) extends Actor with ActorLogging {

  import context.dispatcher

  private[this] val publisher = context.actorOf(Publisher.props(topic))

  var counter: Int = 0
  private[this] val task = context.system.scheduler.schedule(
    0 millis,
    15 seconds,
    self,
    ExecuteTask
  )

  override def postStop() =
    task.cancel()

  override def receive: Receive = {
    case ExecuteTask =>
      counter += 1
      publisher ! Task(counter)
    case PoisonPill =>
      log.info("Terminating Scheduler Task")

  }
}
